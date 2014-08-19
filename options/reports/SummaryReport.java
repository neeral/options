package options.reports;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import options.OptionTransaction;

import org.rendersnake.HtmlCanvas;

class SummaryReport extends AbstractReport {
	private static final int COLUMN_SPACES = 50;

	@Override
	public String createFilename() {
		return "SummaryReport";
	}

	@Override
	void generate(List<OptionTransaction> transactions, HtmlCanvas html) throws IOException {
		final HtmlCanvas body = html.html().body();
		body.h1().content("Option Listings - High-level Summary");

		final HtmlCanvas table = body.table();
		final HtmlCanvas thr = table.tr();
		thr	// for Calls
			.th().content("Expiry")
			.th().content("Strike")
			.th()._th() // sign
			.th().content("Lots")
			.th()._th(); 
		for (int i=0; i<COLUMN_SPACES; i++)
				thr.th()._th(); // spacer 
		thr	// for Puts
			.th().content("Expiry")
			.th().content("Strike")
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
			oe.add(op.callPutFlag(), op.getStrike(), op.buySellFlag(), op.getLots());
		}
		return options;
	}
	
	private void filterClosedPositions(Map<String, OptionsByTicker> options) {
		final Iterator<String> iterTicker = options.keySet().iterator();
		while (iterTicker.hasNext()) {
			final String ticker = iterTicker.next();
			final OptionsByTicker optionsByTicker = options.get(ticker);
			final Iterator<String> iterExpiry = optionsByTicker.map.keySet().iterator();
			while (iterExpiry.hasNext()) {
				final String expiry = iterExpiry.next();
				  
				optionsByTicker.map.get(expiry).removeZeroLots(); 
				
				if (optionsByTicker.map.get(expiry).isEmpty())
					iterExpiry.remove();
			}
			if (optionsByTicker.map.isEmpty())
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
			//		strike --> lots
			final Map<Integer, Integer> calls = oe.calls;
			final Map<Integer, Integer> puts = oe.puts;
			final Iterator<Integer> iterCalls = calls.keySet().iterator();
			final Iterator<Integer> iterPuts = puts.keySet().iterator();			
			
			final int max = Math.max(calls.size(), puts.size());
			
			int callStrike, callLots, putStrike, putLots;
			
			for (int i=0; i<max; i++) {
				if (iterCalls.hasNext()) {
					callStrike = iterCalls.next();
					callLots = calls.get(callStrike);
				} else {
					callStrike = 0;
					callLots = 0;
				}

				if (iterPuts.hasNext()) {
					putStrike = iterPuts.next();
					putLots = puts.get(putStrike);
				} else {
					putStrike = 0;
					putLots = 0;
				}
				
				
				final HtmlCanvas tr = table.tr();
				renderOption(tr, expiry, callStrike, callLots);
				for (int j=0; j<COLUMN_SPACES; j++)
					tr.td()._td(); // spacer
				renderOption(tr, expiry, putStrike, putLots);
				table._tr();
			}
		}
	}
	
	private void renderOption(final HtmlCanvas tr, final String expiry, final Integer strike, int lots) throws IOException {
		if (strike == 0) {
			tr
				.td()._td()
				.td()._td()
				.td()._td()
				.td()._td()
				.td()._td();
		} else {
			tr
				.td().content(expiry)
				.td().content(Integer.toString(strike))
				.td().content(lots > 0 ? "+" : "")
				.td().content(Integer.toString(Math.abs(lots)))
				.td()._td(); // spacer
		}
	}
	
	private class OptionsByTicker {
		Map<String, OptionsByExpiry> map = new LinkedHashMap<>();
	}
	
	private class OptionsByExpiry {
		Map<Integer, Integer> calls = new LinkedHashMap<>();
		Map<Integer, Integer> puts = new LinkedHashMap<>();
		
		void add(char callPutFlag, int strike, String buySellFlag, int lots) {
			Map<Integer, Integer> map = callPutFlag == 'C' ? calls : puts;
			if (map.containsKey(strike)) {
				map.put(strike, sign(buySellFlag) * lots + map.get(strike));
			} else {
				map.put(strike, sign(buySellFlag) * lots);
			}
		}
		
		private int sign(String buySellFlag) {
			return "+".equals(buySellFlag) ? 1 : -1;
		}
		
		void removeZeroLots() {
			removeZeroLots(calls);
			removeZeroLots(puts);
		}
		
		private void removeZeroLots(Map<Integer, Integer> map) {
			Iterator<Integer> it = map.keySet().iterator();
			while (it.hasNext()) {
				int strike = it.next();
				if (map.get(strike) == 0)
					it.remove();
			}
		}
		
		boolean isEmpty() {
			return calls.isEmpty() && puts.isEmpty();
		}
	}

}
