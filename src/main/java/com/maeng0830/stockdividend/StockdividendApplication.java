package com.maeng0830.stockdividend;

import java.io.IOException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockdividendApplication {

	public static void main(String[] args) {
//		SpringApplication.run(StockdividendApplication.class, args);

		try {
			// 데이터를 가져올 주소
			Connection connection = Jsoup.connect("https://finance.yahoo.com/quote/COKE/history?period1=99100800&period2=1667174400&interval=1mo&filter=history&frequency=1mo&includeAdjustedClose=true");
			Document document = connection.get();

			Elements elements = document.getElementsByAttributeValue("data-test", "historical-prices");
			Element element = elements.get(0); // table 태그 전체

			Element tbody = element.children().get(1); // table 내부의 tbody

			for (Element e: tbody.children()) {
				String txt = e.text();
				if (!txt.endsWith("Dividend")) {
					continue;
				}

				String[] splits = txt.split(" ");
				String month = splits[0];
				int day = Integer.valueOf(splits[1].replace(",", ""));
				int year = Integer.valueOf(splits[2]);
				String dividend = splits[3];

				System.out.printf("%d/%s/%d -> %s\n", year, month, day, dividend);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}


	}

}
