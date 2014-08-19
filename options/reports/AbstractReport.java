package options.reports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import options.OptionTransaction;
import options.Utils;

import org.rendersnake.HtmlCanvas;

abstract class AbstractReport implements OptionReport {
	private final static SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("-yyyyMMdd-HHmm");
	private final String reportDir = "output" + File.separator + "reports";

	@Override
	public void generate(List<OptionTransaction> transactions) {
		try {
			final String dir = reportDir + File.separator + createFilename();
			Files.createDirectories(Paths.get(dir));
			Path path = Paths.get(dir + File.separator + createFilename() + TIMESTAMP_FORMAT.format(new Date()) + ".html");
			transactions = filter(transactions);
			try (BufferedWriter w = Files.newBufferedWriter(path, Charset.defaultCharset())) {
				HtmlCanvas html = new HtmlCanvas(w);
				Collections.sort(transactions);
				generate(transactions, html);
				System.out.println("Created " + createFilename() + " report at " + path.toString());
			}
		} catch (IOException e) {
			System.out.println("An error occurred while trying to generate report");
			e.printStackTrace();
		}
	}

	abstract void generate(List<OptionTransaction> transactions, HtmlCanvas html) throws IOException;

	private List<OptionTransaction> filter(List<OptionTransaction> transactions) {
		List<OptionTransaction> filtered = new ArrayList<OptionTransaction>(transactions);
		ListIterator<OptionTransaction> it = filtered.listIterator();
		while (it.hasNext()) {
			OptionTransaction op = it.next();
			if (filter(op))
				it.remove();
		}
		return filtered;
	}

	@Override
	public boolean filter(OptionTransaction op) {
		return Utils.before(Utils.dateToCalendar(op.getExpiry()), Utils.nextExpiryDate());
	}

	
}
