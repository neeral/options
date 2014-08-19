package options.reports;

import java.util.List;

import options.OptionTransaction;

public interface OptionReport {
	void generate(List<OptionTransaction> transactions);
	String createFilename();
	boolean filter(OptionTransaction op);
}
