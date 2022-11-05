package com.maeng0830.stockdividend;

import com.maeng0830.stockdividend.model.Company;
import com.maeng0830.stockdividend.model.ScrapedResult;
import com.maeng0830.stockdividend.scraper.Scraper;
import com.maeng0830.stockdividend.scraper.YahooFinanceScraper;
import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockdividendApplication {

	public static void main(String[] args) {
		SpringApplication.run(StockdividendApplication.class, args);
	}

}
