package options;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

class TransactionFile {
	static List<OptionTransaction> read(String filePath) throws IOException, NumberFormatException, ParseException {
		Path path = Paths.get(filePath);
		
		List<OptionTransaction> transactions = new ArrayList<>();
		
		try (InputStream in = Files.newInputStream(path)) {
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			
			String line;
			boolean firstLine = true;
			while ((line = r.readLine()) !=null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				
				String[] cols = line.trim().split(",");
				
				if (cols.length < 9) {
					System.out.println("This line does not have enough columns: " + line);
					continue;
				}
				
				OptionTransaction opt = new OptionTransaction(
						cols[0],
						cols[1],
						cols[2],
						Integer.valueOf(cols[3]),
						cols[4],
						cols[5],
						Integer.valueOf(cols[6]),
						cols[7],
						Double.valueOf(cols[8]));
				transactions.add(opt);
			}
			
		}
		return transactions;
	}
	
	enum Columns {TRADE_DATE, TICKER, MATURITY};
	
//	public static void printToHtml(String filePath, List<OptionTransaction> transactions) throws IOException {
//		Path path = Paths.get(filePath);
//		try (BufferedWriter w = Files.newBufferedWriter(path, Charset.defaultCharset())) {
//		
//		
//		HtmlCanvas html = new HtmlCanvas(w);
//		HtmlCanvas body = html.html().body();
//		body.h1().content("Our Option Listings");
//		
//		HtmlCanvas table = body.table();
//		table.tr()
//				.th().content("Strike")
//				.th().content("Expiry")
//				.th()._th()
//				.th().content("Lots")
//				.th().content("Price")
//			._tr();
//		for (OptionTransaction op : transactions) {
//			table.tr()
//					.td().content(Integer.toString(op.strike))
//					.td().content(op.maturity)
//					.td().content(Buy_Sell.BUY.equals(op.buy_sell) ? "+" : "")
//					.td().content(op.no_of_lots + " " + op.call_put.name().charAt(0))
//					.td().content(Double.toString(op.premium))
//				._tr();
//		}
//		table.close();
//		html._body()._html();
//		
//		}
//		
//	}
	
//	String ticker = null;
//	for (OptionTransaction op : transactions) {
//		if (ticker == null || !ticker.equals(op.getTicker())) {
//			ticker = op.getTicker();
//			table
//				.tr()
//					.td().h2().content(ticker)._td()
//				._tr();
//		}
//		
//		table.tr()
//				.td().content(op.getExpiryAsString())
//				.td().content(Integer.toString(op.getStrike()))
//				.td().content(op.getAccount())
//				.td().content(op.buySellFlag())
//				.td().content(Integer.toString(op.getLots()))
//				.td()._td()
//			._tr();
//	}
}
