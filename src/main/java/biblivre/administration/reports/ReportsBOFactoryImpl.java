package biblivre.administration.reports;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

import biblivre.administration.reports.configuration.ReportsConfiguration;

@Service
public class ReportsBOFactoryImpl implements ReportsBOFactory {

	private ReportsDAOFactory reportsDAOFactory;

	public ReportsBOFactoryImpl() {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
				ReportsConfiguration.class)) {

			this.reportsDAOFactory = (ReportsDAOFactory) context.getBean("reportsDAOFactory");
		}
	}

	@Override
	public ReportsBO getInstance(String schema) {
		ReportsBO reportsBOImpl = new ReportsBOImpl(reportsDAOFactory.getInstance(schema), schema);

		return reportsBOImpl;
	}
}
