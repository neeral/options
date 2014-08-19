package options.reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import options.OptionTransaction;

import org.rendersnake.HtmlCanvas;

/**
 * This report shows the open positions. It has two columns on the page, one for
 * calls and one for puts. It shows the Expiry, Strike, Account and no of Lots
 * for each Company.
 * 
 * @author neeral
 * 
 */
class StandardReport extends AbstractReport {
	private static final int COLUMN_SPACES = 20;

	@Override
	public void generate(List<OptionTransaction> transactions, HtmlCanvas html) throws IOException {
		final HtmlCanvas body = html.html().body();
		body.h1().content("Option Listings");

		final HtmlCanvas table = body.table();
		final HtmlCanvas thr = table.tr();
		thr	// for Calls
			.th().content("Expiry")
			.th().content("Strike")
			.th().content("Account")
			.th()._th() // sign
			.th().content("Lots")
			.th()._th(); 
		for (int i=0; i<COLUMN_SPACES; i++)
				thr.th()._th(); // spacer for Puts
		thr
			.th().content("Expiry")
			.th().content("Strike")
			.th().content("Account")
			.th()._th() // sign
			.th().content("Lots");
		table._tr();
		
		final Map<String, OptionsByTicker> options = structureOptionsByMap(transactions);
		filterClosedPositions(options);
		
		for (String ticker : options.keySet()) {
			for (int i=0; i<10; i++) // these create a gap between the last ticker's data and the next ones.
				table.tr()._tr();
			renderTicker(ticker, options.get(ticker), table);
		}

		table.close();
		html._body()._html();

	}

	@Override
	public String createFilename() {
		return "StandardReport";
	}

	private Map<String, OptionsByTicker> structureOptionsByMap(List<OptionTransaction> transactions) {
		final Map<String, OptionsByTicker> options = new LinkedHashMap<>();
		for (OptionTransaction op : transactions) {
			
			final String ticker = op.getTicker();
			if (!options.containsKey(ticker))
				options.put(ticker, new OptionsByTicker());
			
			final OptionsByTicker optionsByTicker = options.get(ticker);
			
			final String expiry = op.getExpiryAsString();
			if (!optionsByTicker.map.containsKey(expiry))
				optionsByTicker.map.put(expiry, new OptionsByExpiry());
			
			final OptionsByExpiry oe = optionsByTicker.map.get(expiry);
			
			oe.add(op.callPutFlag(), new Option(op.getStrike(), op.getAccount()), op.buySellFlag(), op.getLots());
		}
		return options;
	}
	
	private void filterClosedPositions(Map<String, OptionsByTicker> options) {
		Iterator<String> iterTicker = options.keySet().iterator();
		while (iterTicker.hasNext()) {
			final String ticker = iterTicker.next();
			final OptionsByTicker ot = options.get(ticker);
			Iterator<String> iterExpiry = ot.map.keySet().iterator();
			while (iterExpiry.hasNext()) {
				final String expiry = iterExpiry.next();
				final OptionsByExpiry oe = ot.map.get(expiry);
				
				oe.removeZeroLots();
				
				if (oe.isEmpty())
					iterExpiry.remove();
			}
			if (ot.map.isEmpty())
				iterTicker.remove();
		}
	}
	
	private void renderTicker(final String ticker, final OptionsByTicker o, final HtmlCanvas table) throws IOException {
		table
			.tr()
				.td().h2().content(ticker)._td()
			._tr();
		
		for (String expiry : o.map.keySet()) {
			final OptionsByExpiry oe = o.map.get(expiry);
			final List<Option> calls = new ArrayList<Option>(oe.calls.keySet());
			final List<Option> puts = new ArrayList<Option>(oe.puts.keySet());
			
			final int max = Math.max(calls.size(), puts.size());
			
			for (int i=0; i<max; i++) {
				Option call = i < calls.size() ? calls.get(i) : null;
				Option put = i < puts.size() ? puts.get(i) : null;
				
				final HtmlCanvas tr = table.tr();
				renderOption(tr, expiry, call, oe.calls.get(call));
				for (int j=0; j<COLUMN_SPACES; j++)
					tr.td()._td(); // spacer
				renderOption(tr, expiry, put, oe.puts.get(put));
				table._tr();
			}
		}
	}
	
	private void renderOption(final HtmlCanvas tr, final String expiry, final Option op, final Integer lots) throws IOException {
		if (op == null) {
			tr
				.td()._td()
				.td()._td()
				.td()._td()
				.td()._td()
				.td()._td()
				.td()._td();
		} else {
			tr
				.td().content(expiry)
				.td().content(Integer.toString(op.strike))
				.td().content(op.account)
				.td().content(lots > 0 ? "+" : "")
				.td().content(Integer.toString(Math.abs(lots)))
				.td()._td(); // spacer
		}
	}
	
	private class OptionsByTicker {
		Map<String, OptionsByExpiry> map = new LinkedHashMap<>();
	}
	
	private class OptionsByExpiry {
		Map<Option, Integer> calls = new LinkedHashMap<>();
		Map<Option, Integer> puts = new LinkedHashMap<>();
		
		void add(char callPutFlag, Option o, String buySellFlag, int lots) {
			final Map<Option, Integer> map = callPutFlag == 'C' ? calls : puts;
			if (!map.containsKey(o))
				map.put(o, sign(buySellFlag) * lots);
			else
				map.put(o, sign(buySellFlag) * lots + map.get(o));
				
		}
		
		private int sign(String buySellFlag) {
			return "+".equals(buySellFlag) ? 1 : -1;
		}
		
		void removeZeroLots() {
			removeZeroLots(calls);
			removeZeroLots(puts);
		}
		
		private void removeZeroLots(Map<Option, Integer> map) {
			Iterator<Option> it = map.keySet().iterator();
			while (it.hasNext()) {
				Option op = it.next();
				if (map.get(op) == 0)
					it.remove();
			}
		}
		
		boolean isEmpty() {
			return calls.isEmpty() && puts.isEmpty();
		}
	}
	
	private class Option {
		int strike;
		String account;
		
		Option(int strike, String account) {
			this.strike = strike;
			this.account = account;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result *= prime;
			result += account.hashCode();
			result *= prime;
			result += strike;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (null == obj) return false;
			if (!(obj instanceof Option)) return false;
			Option o = (Option) obj;
			return strike == o.strike && account.equals(o.account);
		}
		
		
	}

}
