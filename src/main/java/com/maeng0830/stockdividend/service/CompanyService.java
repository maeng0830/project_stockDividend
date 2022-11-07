package com.maeng0830.stockdividend.service;

import com.maeng0830.stockdividend.model.Company;
import com.maeng0830.stockdividend.model.ScrapedResult;
import com.maeng0830.stockdividend.persist.entity.CompanyEntity;
import com.maeng0830.stockdividend.persist.entity.DividendEntity;
import com.maeng0830.stockdividend.persist.repository.CompanyRepository;
import com.maeng0830.stockdividend.persist.repository.DividendRepository;
import com.maeng0830.stockdividend.scraper.Scraper;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@AllArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper yahooFinanceScraper;

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker) {
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists) {
            throw new RuntimeException("already exists ticker -> " + ticker);
        }

        return this.storeCompanyAndDividend(ticker);
    }

    public Page<CompanyEntity> getAllCompany(Pageable pageable) {
        return this.companyRepository.findAll(pageable);
    }

    private Company storeCompanyAndDividend(String ticker) {
        // ticker를 기준으로 회사 스크래핑
        Company company = this.yahooFinanceScraper.scrapCompanyByTicker(ticker);

        if (ObjectUtils.isEmpty(company)) {
            throw new RuntimeException("failed to scrap ticker -> " + ticker);
        }

        // 회사가 존재할 경우, 회사의 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(company);

        // 스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividends().stream()
            .map(e -> new DividendEntity(companyEntity.getId(), e))
            .collect(Collectors.toList());

        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }

    public void addAutocompleteKeyword(String keyword) {
        this.trie.put(keyword, null);
    }

    // sql문을 이용한 자동완성
    public List<String> getCompanyNamesByKeyword(String keyword) {
        Pageable limit = PageRequest.of(0, 10);

        Page<CompanyEntity> companyEntities = this.companyRepository
            .findByNameStartingWithIgnoreCase(keyword, limit);

        return companyEntities.stream()
            .map(e -> e.getName())
            .collect(Collectors.toList());
    }

    // trie를 이용한 자동완성
    public List<String> autocomplete(String keyword) {
        return (List<String>) this.trie.prefixMap(keyword).keySet()
            .stream().limit(10).collect(Collectors.toList());
    }

    // trie를 이용한 자동완성
    public void deleteAutocompleteKeyword(String keyword) {

        this.trie.remove(keyword);
    }

    // 회사 정보 삭제
    public String deleteCompany(String ticker) {
        CompanyEntity company = this.companyRepository.findByTicker(ticker)
            .orElseThrow(() -> new RuntimeException("존재하지 않는 회사입니다."));

        this.dividendRepository.deleteAllByCompanyId(company.getId());
        this.companyRepository.delete(company);

        this.deleteAutocompleteKeyword(company.getName());

        return company.getName();
    }
}
