package biblivre.core.utils;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PgDumpCommand {

	public PgDumpCommand(File executable, InetSocketAddress address, Charset encoding,
			Format format, String schema, File file,	boolean isSchemaOnly,
			boolean isDataOnly, String excludeTablePattern,
			String includeTablePattern, String username, String dbname) {

		this.executable = executable;
		this.address = address;
		this.encoding = encoding;
		this.format = format;
		this.schema = schema;
		this.file = file;
		this.isSchemaOnly = isSchemaOnly;
		this.isDataOnly = isDataOnly;
		this.excludeTablePattern = excludeTablePattern;
		this.tablePattern = includeTablePattern;
		this.username = username;
		this.dbname = dbname;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public Charset getEncoding() {
		return encoding;
	}

	public Format getFormat() {
		return format;
	}

	public String getSchema() {
		return schema;
	}

	public File getFile() {
		return file;
	}

	public boolean isSchemaOnly() {
		return isSchemaOnly;
	}

	public boolean isDataOnly() {
		return isDataOnly;
	}

	public String getExcludeTablePattern() {
		return excludeTablePattern;
	}

	public String getTablePattern() {
		return tablePattern;
	}

	public File getExecutable() {
		return this.executable;
	}
	
	public String getUsername() {
		return username;
	}

	public String getDbname() {
		return dbname;
	}

	public List<String> getCommands() {
		List<String> result = new ArrayList<String>();

		result.add(getExecutable().getAbsolutePath());

		result.add(Option.HOST.toString() + getAddress().getHostString());

		result.add(Option.PORT.toString() + String.valueOf(getAddress().getPort()));
		
		result.add(Option.USERNAME.toString() + getUsername());
		
		result.add(Option.DBNAME.toString() + getDbname());
		
		result.add(Option.ENCODING.toString() + getEncoding().name());

		result.add(Option.FORMAT.toString() + getFormat().value());

		result.add(Option.SCHEMA.toString() + getSchema());

		result.add(Option.FILE.toString() + getFile().getAbsolutePath());

		if (isSchemaOnly()) {
			result.add(Option.SCHEMA_ONLY.toString());
		}

		if (isDataOnly()) {
			result.add(Option.DATA_ONLY.toString());
		}

		if (getExcludeTablePattern() != null) {
			result.add(Option.EXCLUDE_TABLE.toString() + getExcludeTablePattern());
		}

		if (getTablePattern() != null) {
			result.add(Option.TABLE.toString() + getTablePattern());
		}

		return result;
	}

	public enum Format {
		PLAIN, CUSTOM, DIRECTORY, TAR;

		String value() {
			return this.name().toLowerCase();
		}
	}

	enum Option {
		ENCODING(true), FORMAT(true), SCHEMA(true), FILE(true), SCHEMA_ONLY(false), DATA_ONLY(false), EXCLUDE_TABLE(true),
		HOST(true), PORT(true), TABLE(true), USERNAME(true), DBNAME(true);
		
		private final boolean equalString;
		
		private Option(boolean equalString) {
			this.equalString = equalString;
		}

		public String toString() {
			return "--" + this.name().replaceAll("_", "-").toLowerCase() + (this.equalString ? "=" : "");
		}
	}

	private final File executable;
	private final InetSocketAddress address;
	private final Charset encoding;
	private final Format format;
	private final String schema;
	private final File file;
	private final boolean isSchemaOnly;
	private final boolean isDataOnly;
	private final String excludeTablePattern;
	private final String tablePattern;
	private final String username;
	private final String dbname;
}
