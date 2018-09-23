/*******************************************************************************
 * Este arquivo é parte do Biblivre5.
 * 
 * Biblivre5 é um software livre; você pode redistribuí-lo e/ou 
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como 
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da 
 * Licença, ou (caso queira) qualquer versão posterior.
 * 
 * Este programa é distribuído na esperança de que possa ser  útil, 
 * mas SEM NENHUMA GARANTIA; nem mesmo a garantia implícita de
 * MERCANTIBILIDADE OU ADEQUAÇÃO PARA UM FIM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja em <http://www.gnu.org/licenses/>.
 * 
 * @author Alberto Wagner <alberto@biblivre.org.br>
 * @author Danniel Willian <danniel@biblivre.org.br>
 ******************************************************************************/
package biblivre.core.update;

import java.sql.Connection;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import biblivre.administration.indexing.IndexingBO;
import biblivre.administration.indexing.IndexingGroups;
import biblivre.cataloging.Fields;
import biblivre.cataloging.enums.RecordType;
import biblivre.core.configurations.Configurations;
import biblivre.core.configurations.ConfigurationsDTO;
import biblivre.core.schemas.Schemas;
import biblivre.core.translations.Translations;
import biblivre.core.utils.Constants;
import biblivre.core.utils.TextUtils;

public class Updates {

	public static String getVersion() {
		return Constants.BIBLIVRE_VERSION;
	}

	public static boolean globalUpdate() {
		return _doUpdates(updater -> updater.globalUpdate());
	}

	public static boolean schemaUpdate(String schema) {
		return _doUpdates(updater -> updater.schemaUpdate(schema));
	}

	public static String getUID() {
		String uid = Configurations.getString(Constants.GLOBAL_SCHEMA, Constants.CONFIG_UID);

		if (StringUtils.isBlank(uid)) {
			uid = UUID.randomUUID().toString();

			ConfigurationsDTO config = new ConfigurationsDTO();
			config.setKey(Constants.CONFIG_UID);
			config.setValue(uid);
			config.setType("string");
			config.setRequired(false);

			Configurations.save(Constants.GLOBAL_SCHEMA, config, 0);
		}

		return uid;
	}

	public static String checkUpdates() {
		String uid = Updates.getUID();
		String version = Updates.getVersion();

		PostMethod updatePost = new PostMethod(Constants.UPDATE_URL);
		updatePost.addParameter("uid", TextUtils.biblivreEncrypt(uid));
		updatePost.addParameter("version", TextUtils.biblivreEncrypt(version));

		HttpClient client = new HttpClient();
		client.getHttpConnectionManager().getParams().setConnectionTimeout(3000);
		try {
			int status = client.executeMethod(updatePost);

			if (status == HttpStatus.SC_OK) {
				return updatePost.getResponseBodyAsString();
			}
			updatePost.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	public static void fixPostgreSQL81() {
		UpdatesDAO dao = UpdatesDAO.getInstance("public");

		try {
			if (!dao.checkFunctionExistance("array_agg")) {
				String version = dao.getPostgreSQLVersion();

				if (version.contains("8.1")) {
					dao.create81ArrayAgg();
				} else {
					dao.createArrayAgg();
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static boolean _doUpdates(Function<UpdateBO, Boolean> updater) {
		return _getUpdateBOSet().stream()
				.allMatch(updateBO -> {
					try {
						final UpdateBO newInstance = updateBO.newInstance();

						return updater.apply(newInstance);
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}

					return false;
				});
	}

	private static Set<Class<? extends UpdateBO>> _getUpdateBOSet() {
		final String packageName = Updates.class.getPackage().getName();
		Reflections reflections = new Reflections(packageName);

		return reflections.getSubTypesOf(UpdateBO.class);
	}
}
