package com.maeng0830.stockdividend.scraper;

import com.maeng0830.stockdividend.model.Company;
import com.maeng0830.stockdividend.model.ScrapedResult;

public interface Scraper {
    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);
}
