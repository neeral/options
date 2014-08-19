package options;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import options.reports.ReportFactory;

public class PortfolioManager {

	public static void main(String[] args) throws NumberFormatException, IOException, ParseException {
		if (args.length < 1) {
			usage();
			System.exit(1);
		}
		
		String filePath = args[0];
		
		System.out.print("Starting ... ");
		List<OptionTransaction> transactions = TransactionFile.read(filePath);
		System.out.println(" read file with " + transactions.size() + " rows.");
		
		ReportFactory.generateReport("CurrentMonthStandardReport", transactions);
		ReportFactory.generateReport("", transactions);
		ReportFactory.generateReport("Summary", transactions);
		ReportFactory.generateReport("Transaction", transactions);
		
		System.out.println("Finished.");

		
	}

	private static void usage() {
		System.out.println("Please provide the name of the CSV file containing raw options data.\n"
				+ "e.g. option-reports.jar C:\\My Documents\\options\\ADM_listing.csv");
	}
}
