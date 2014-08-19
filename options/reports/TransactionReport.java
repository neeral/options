package options.reports;

import java.io.IOException;

import static org.rendersnake.HtmlAttributesFactory.color;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import options.OptionTransaction;

import org.rendersnake.HtmlCanvas;

class TransactionReport extends AbstractReport {

	@Override
	public String createFilename() {
		return "TransactionReport";
	}

	@Override
	void generate(List<OptionTransaction> transactions, HtmlCanvas html) throws IOException {
		Collections.sort(transactions, new OptionComparator());
		
		final HtmlCanvas body = html.html().body();
		body.h1().content("Option Listings");

		final HtmlCanvas table = body.table();
		final HtmlCanvas thr = table.tr();
		thr
			.th().content("Expiry")
			.th().content("Strike")
			.th().content("Account")
			.th()._th() // sign
			.th().content("Lots")
			.th().content("TradeDate")
			.th().content("Premium/p");
		table._tr();
		
		insertBlankRow(table);
		
		String previousTicker = null;
		for (OptionTransaction op : transactions) {
			final String ticker = op.getTicker();
			if (!ticker.equals(previousTicker)) {
				renderTicker(table, ticker);
			}
			renderOption(table, op);
			previousTicker = ticker;
		}

		table.close();
		html._body()._html();

	}

	@SuppressWarnings("deprecation")
	private void insertBlankRow(HtmlCanvas table) throws IOException {
		table.tr()
			.td().font(color("white")).content("Expiry Date")._td()
			.td().font(color("white")).content("Strike C/P")._td()
			.td().font(color("white")).content("Account")._td()
			.td().font(color("white")).content("B/S")._td()
			.td().font(color("white")).content("Lots------")._td()
			.td().font(color("white")).content("TradeDate--------")._td()
			.td().font(color("white")).content("Premium")._td()
		._tr();
	}

	private void renderTicker(final HtmlCanvas table, final String ticker) throws IOException {
		for (int i=0; i<10; i++) // these create a gap between the last ticker's data and the next ones.
			table.tr()._tr();
		table
			.tr()
				.td().h2().content(ticker)._td()
			._tr();
	}
	
	private void renderOption(final HtmlCanvas table, final OptionTransaction op) throws IOException {
		table.tr()
			.td().content(op.getExpiryAsString())
			.td().content(Integer.toString(op.getStrike()) + " " + op.callPutFlag())
			.td().content(op.getAccount())
			.td().content(op.buySellFlag())
			.td().content(Integer.toString(op.getLots()))
			.td().content(op.getTradeDateAsString())
			.td().content(Double.toString(op.getPremium()))
		._tr();
	}
	
	private static class OptionComparator implements Comparator<OptionTransaction> {
		@Override
		public int compare(OptionTransaction a, OptionTransaction b) {
			int c;
			
			c = a.getTicker().compareTo(b.getTicker());
			if (c != 0) return c;

			c = a.callPutFlag() - b.callPutFlag();
			if (c != 0) return c;
			
			c = a.getExpiry().compareTo(b.getExpiry());
			if (c != 0) return c;
			
			c = a.getStrike() - b.getStrike();
			if (c != 0) return c;
				
			c = a.getTradeDate().compareTo(b.getTradeDate());
			if (c != 0) return c;
			
			return c;
		}
		
	}

}
