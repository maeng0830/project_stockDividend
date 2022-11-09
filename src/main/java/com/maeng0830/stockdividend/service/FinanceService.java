package com.maeng0830.stockdividend.service;

import com.maeng0830.stockdividend.exception.customException.NoCompanyException;
import com.maeng0830.stockdividend.model.Company;
import com.maeng0830.stockdividend.model.Dividend;
import com.maeng0830.stockdividend.model.ScrapedResult;
import com.maeng0830.stockdividend.model.constants.CacheKey;
import com.maeng0830.stockdividend.persist.entity.CompanyEntity;
import com.maeng0830.stockdividend.persist.entity.DividendEntity;
import com.maeng0830.stockdividend.persist.repository.CompanyRepository;
import com.maeng0830.stockdividend.persist.repository.DividendRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 동일한 데이터에 대한 요청이 빈번하게 들어온다 -> 캐시로 저장해두면 훨씬 빠른 응답
    // 자주 변경되는 데이터가 아니다 -> 캐시로 한번 저장해두면 따로 수정할 필요가 거의 없다.
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        log.info("search company -> " + companyName);
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
            .orElseThrow(() -> new NoCompanyException());

        // 2. 회사 아이디를 통해 배당금 조회

        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(
            company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = new ArrayList<>();
        for (DividendEntity entity : dividendEntities) {
            dividends.add(new Dividend(entity.getDate(), entity.getDividend()));
        }

        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);
    }
}
