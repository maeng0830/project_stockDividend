package com.maeng0830.stockdividend.service;

import com.maeng0830.stockdividend.model.Company;
import com.maeng0830.stockdividend.model.Dividend;
import com.maeng0830.stockdividend.model.ScrapedResult;
import com.maeng0830.stockdividend.persist.entity.CompanyEntity;
import com.maeng0830.stockdividend.persist.entity.DividendEntity;
import com.maeng0830.stockdividend.persist.repository.CompanyRepository;
import com.maeng0830.stockdividend.persist.repository.DividendRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public ScrapedResult getDividendByCompanyName(String companyName) {
        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 회사명입니다."));

        // 2. 회사 아이디를 통해 배당금 조회

        List<DividendEntity> dividendEntities = this.dividendRepository.findAllByCompanyId(
            company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = new ArrayList<>();
        for (DividendEntity entity : dividendEntities) {
            dividends.add(Dividend.builder()
                .date(entity.getDate())
                .dividend(entity.getDividend())
                .build());
        }

        return new ScrapedResult(Company.builder()
            .ticker(company.getTicker())
            .name(company.getName())
            .build(),
            dividends);
    }
}
