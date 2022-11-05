package com.maeng0830.stockdividend.scheduler;

import com.maeng0830.stockdividend.model.Company;
import com.maeng0830.stockdividend.model.ScrapedResult;
import com.maeng0830.stockdividend.persist.entity.CompanyEntity;
import com.maeng0830.stockdividend.persist.entity.DividendEntity;
import com.maeng0830.stockdividend.persist.repository.CompanyRepository;
import com.maeng0830.stockdividend.persist.repository.DividendRepository;
import com.maeng0830.stockdividend.scraper.Scraper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    private final Scraper yahooFinanceScraper;

    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("scraping scheduler is started");
        // 저장된 회사 목록 조회
        List<CompanyEntity> companies = this.companyRepository.findAll();

        // 회사별 배당금 정보 스크래핑
        for (CompanyEntity company : companies) {
            log.info("scraping: " + company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(Company.builder()
                .name(company.getName())
                .ticker(company.getTicker())
                .build());

            scrapedResult.getDividends().stream()
                .map(e -> new DividendEntity(company.getId(), e))
                .forEach(e -> {
                    boolean exists = this.dividendRepository.existsByCompanyIdAndDate(
                        e.getCompanyId(),
                        e.getDate());
                    if (!exists) {
                        this.dividendRepository.save(e);
                    }
                });

            // 연속적인 스크래핑 요청을 하지 않도록 일시 정지
            try {
                Thread.sleep(3000); // 3초
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        log.info("scraping scheduler is done");
    }
}
