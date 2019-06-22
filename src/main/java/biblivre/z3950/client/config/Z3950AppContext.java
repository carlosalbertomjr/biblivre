package biblivre.z3950.client.config;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.jzkit.a2j.codec.util.OIDRegister;
import org.jzkit.configuration.provider.xml.XMLImpl;
import org.jzkit.search.util.Profile.ProfileServiceImpl;
import org.jzkit.search.util.RecordConversion.FragmentTransformerService;
import org.jzkit.util.PropsHolder;
import org.jzkit.z3950.QueryModel.PropsBasedInternalToType1ConversionRules;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Z3950AppContext {
	@Bean
	public OIDRegister OIDRegister() {
		return new OIDRegister("/a2j.properties");
	}
	
	@Bean
	public PropsHolder RPNToInternalRules() {
		return new PropsHolder("/InternalAttrTypes.properties");
	}
	
	@Bean
	public PropsBasedInternalToType1ConversionRules InternalToType1ConversionRules() {
		return new PropsBasedInternalToType1ConversionRules("/InternalToType1Rules.properties");
	}
	
	@Bean
	public XMLImpl JZKitConfig() {
		return new XMLImpl("/z3950Config.xml");
	}
	
	@Bean
	public FragmentTransformerService TransformationService() {
		return new FragmentTransformerService(JZKitConfig());
	}
	
	@Bean
	public ProfileServiceImpl ProfileService() {
		return new ProfileServiceImpl();
	}
	
	@Bean
	public DataSource z3950DataSource() throws NamingException {
		InitialContext cxt = new InitialContext();
		DataSource ds = (DataSource) cxt.lookup("java:comp/env/jdbc/biblivre4");
		return ds;
	}
}