package com.screamatthewind.email_pimd_log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.*;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/

import java.io.File;

import com.screamatthewind.utils.EmailUtil;
import com.screamatthewind.utils.ExceptionHandler;
import com.screamatthewind.yaml.Configuration;

class MainActivity {

//	private static final Logger LOGGER = LoggerFactory.getLogger(MainActivity.class);

	static Configuration config = null;

	public static void main(String[] args) throws Exception {

		if (args.length != 1) {
			System.out.println("Please provide a yaml configuration file argument");
			return; // exit program
		}

		ReadConfigFile(args[0]);

		EmailUtil.sendLogFileEmail(config.getEMailConfig(), config.getLogFilename());
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