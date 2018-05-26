package com.screamatthewind.email_stb_usage;

import java.sql.*; /* The class files under this package helps to pull data out from Oracle table */
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter; /* Required for the CSV output generation */

import com.opencsv.CSVWriter;
import com.screamatthewind.utils.EmailUtil;
import com.screamatthewind.utils.ExceptionHandler;
import com.screamatthewind.yaml.Agent;
import com.screamatthewind.yaml.Configuration;
import com.screamatthewind.yaml.DatabaseConfig;
import com.screamatthewind.yaml.Reports;

class MainActivity {

	private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

	static Configuration config = null;
	static Connection dbConnection;

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out.println("Please provide a yaml configuration file argument");
			return; // exit program
		}

		ReadConfigFile(args[0]);

		Class.forName("com.mysql.jdbc.Driver");

		DatabaseConfig dbConfig = config.getDatabaseConfig();
		String connection = String.format("jdbc:mysql://%s:%d/%s?useSSL=false", dbConfig.getHost(), dbConfig.getPort(),
				dbConfig.getName());
		dbConnection = DriverManager.getConnection(connection, dbConfig.getUsername(), dbConfig.getPassword());

		List<Agent> agents = new ArrayList<Agent>();

		List<Reports> reports = config.getReports();
		for (Reports report : reports) {

			for (Agent agent : report.getAgents()) {
				if (!agents.contains(agent))
					agents.add(new Agent(agent.getAgentName(), false));
			}
		}

		// add exclusions to agents
		for (String exclusion : config.getExclusions()) {
			if (!agents.contains(exclusion))
				agents.add(new Agent(exclusion, true));
		}

		for (Agent agent : agents) {
			if (agent.getExclude() == false)
				agent.setFilename(CreateReport(agent.getAgentName()));
		}

		// update the report filenames
		for (Reports report : reports) {
			for (Agent reportAgent : report.getAgents()) {
				for (Agent agent : agents) {
					if (agent.getExclude() == false) {
						if (reportAgent.getAgentName().equals(agent.getAgentName())) {
							reportAgent.setFilename(agent.getFilename());
							reportAgent.setExclude(agent.getExclude());
						}
					}
				}
			}
		}

		for (Reports report : reports) {
			EmailUtil.sendReportEmail(config.getEMailConfig(), report);
		}

		List<String> missingAgents = validateAgents(agents);
		if (missingAgents.size() > 0)
			EmailUtil.sendMissingAgentsEmail(config.getEMailConfig(), missingAgents);

		dbConnection.close();

		LOGGER.info("done");
	}

	private static List<String> validateAgents(List<Agent> agents) {

		LOGGER.info("Validating Agents");

		List<String> missingAgents = new ArrayList<String>();

		try {

			String agentName;
			Statement stmt = dbConnection.createStatement();

			try {

				String sql = "select distinct(agent) as agent from v_itv_agent_channels_summary order by agent, channel_name";

				ResultSet rs = stmt.executeQuery(sql);
				try {

					while (rs.next()) {

						agentName = rs.getString("agent");

						Boolean foundAgent = false;
						for (Agent agent : agents) {
							if (agentName.equals(agent.getAgentName())) {
								foundAgent = true;
								break;
							}
						}

						if (foundAgent == false) {
							missingAgents.add(agentName);
							LOGGER.error(
									"Agent " + agentName + " not found during validation.  Need to update YAML file.");
						}
					}
				} finally {
					try {
						rs.close();
					} catch (Exception ignore) {
					}
				}
			} finally {
				try {
					stmt.close();
				} catch (Exception ignore) {
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleException("validateAgents", null, e, MainActivity.class);
		}

		return missingAgents;

	}

	private static String CreateReport(String agentName) {

		String filename = "";
		String dateStamp = "";

		LOGGER.info("Creating Report for Agent " + agentName);

		try {

			Statement stmt = dbConnection.createStatement();

			try {

				String sql = String.format(
						"select * from v_itv_agent_channels_summary where agent = '%s' order by agent, channel_name",
						agentName);

				ResultSet query_set = stmt.executeQuery(sql);
				try {

					dateStamp = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
					filename = String.format("%s%s_%s.csv", config.getReportDirectory(), agentName.replace(" ", "_"), dateStamp);

					FileWriter my_csv = new FileWriter(filename);
					CSVWriter my_csv_output = new CSVWriter(my_csv);

					my_csv_output.writeAll(query_set, true);
					my_csv_output.close();

				} catch (Exception e) {
					ExceptionHandler.handleException("CreateReport", null, e, MainActivity.class);
				} finally {
					try {
						query_set.close();
					} catch (Exception ignore) {
					}
				}
			} finally {
				try {
					stmt.close();
				} catch (Exception ignore) {
				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleException("CreateReport", null, e, MainActivity.class);
		}

		return filename;
	}

	private static void ReadConfigFile(String filename) {
		File file = new File(filename);

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

		try {
			config = mapper.readValue(file, Configuration.class);
		} catch (Exception e) {
			ExceptionHandler.handleException("ReadConfigFile", null, e, MainActivity.class);
		}
	}

}