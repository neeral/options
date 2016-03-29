package options;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OptionTransaction implements Comparable<OptionTransaction> {
	
	private static final SimpleDateFormat TRADE_DATE_FORMAT = new SimpleDateFormat("dd/MM/yy");
	private static final SimpleDateFormat TRADE_DATE_FORMAT_2 = new SimpleDateFormat("dd-MMM-yy");
	private static final SimpleDateFormat EXPIRY_DATE_FORMAT = new SimpleDateFormat("MMM-yy");

	OptionTransaction(String trade_date, String ticker, String expiry, int strike, String buy_sell, String call_put, int no_of_lots,
			String account, double premium, String raw) throws ParseException {

        try {
		    this.trade_date = TRADE_DATE_FORMAT.parse(trade_date);
        } catch (ParseException pe) {
            try {
                this.trade_date = TRADE_DATE_FORMAT_2.parse(trade_date);
            } catch (ParseException pe2) {
                System.err.println("The trade date must be in the format dd/MM/yy or dd-MMM-yy. However we have an " + pe2.getMessage() + ". This error is caused by the line: " + raw);
                throw pe2;
            }
        }

		this.ticker = ticker;
		this.expiry =  EXPIRY_DATE_FORMAT.parse(expiry);
		this.strike = strike;
		this.buy_sell = Buy_Sell.valueOf(buy_sell);
		this.call_put = OptionType.valueOf(call_put);
		if (no_of_lots > 0)
			this.no_of_lots = no_of_lots;
		this.account = account;
		this.premium = Math.abs(premium);
	}
	
	private Date trade_date;
	private String ticker;
	private Date expiry;
	private int strike;
	private Buy_Sell buy_sell;
	private OptionType call_put;
	private int no_of_lots;
	private String account;
	private double premium; // always a positive number

	enum Buy_Sell { BUY, SELL };
	enum OptionType { CALL, PUT };

	String quantity() {
		StringBuffer sb = new StringBuffer();
		if (Buy_Sell.BUY.equals(buy_sell))
			sb.append('+');
		else sb.append("-");
		sb.append(no_of_lots).append(' ');
		sb.append(call_put.name().charAt(0));
		return sb.toString();
	}
	
	public String buySellFlag() {
		return Buy_Sell.BUY == buy_sell ? "+" : "";
	}
	
	public char callPutFlag() {
		return call_put.name().charAt(0);
	}

	public Date getTradeDate() {
		return trade_date;
	}

	public String getTradeDateAsString() {
		return TRADE_DATE_FORMAT.format(trade_date);
	}
	
	public String getTicker() {
		return ticker;
	}

	public Date getExpiry() {
		return expiry;
	}

	public String getExpiryAsString() {
		return EXPIRY_DATE_FORMAT.format(expiry);
	}
	
	public int getStrike() {
		return strike;
	}

	public Buy_Sell getBuy_sell() {
		return buy_sell;
	}

	public OptionType getCall_put() {
		return call_put;
	}

	public int getLots() {
		return no_of_lots;
	}

	public String getAccount() {
		return account;
	}

	public double getPremium() {
		return premium;
	}

	@Override
	public int compareTo(OptionTransaction b) {
		int c;
		
		c = getTicker().compareTo(b.getTicker());
		if (c != 0) return c;
		
		c = getExpiry().compareTo(b.getExpiry());
		if (c != 0) return c;
		
		c = getAccount().compareTo(b.getAccount());
		if (c != 0) return c;
		
		c = getStrike() - b.getStrike();
		
		return c;
	}
}
