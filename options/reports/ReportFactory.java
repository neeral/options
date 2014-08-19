package options.reports;

import java.util.List;

import options.OptionTransaction;
import options.Utils;

public class ReportFactory {
	public static void generateReport(String reportType, List<OptionTransaction> transactions) {
		OptionReport report;
		switch (reportType) {
			case "StandardReport" : report = new StandardReport(); break;
			case "Summary" : report = new SummaryReport(); break;
			case "Transaction" : report = new TransactionReport(); break;
			case "CurrentMonthStandardReport" : report = new StandardReport() {
				@Override
				public boolean filter(OptionTransaction op) {
					return !Utils.equals(Utils.dateToCalendar(op.getExpiry()), Utils.nextExpiryDate());
				}

				@Override
				public String createFilename() {
					return "CurrentMonth";
				}
				
				
			}; break;
			default:  report = new StandardReport(); break;
		}
		report.generate(transactions);
	}
}
