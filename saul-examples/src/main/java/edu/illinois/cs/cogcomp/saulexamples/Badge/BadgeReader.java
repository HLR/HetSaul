package edu.illinois.cs.cogcomp.saulexamples.Badge;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BadgeReader {
	public List<String> badges;
	// int currentBadge;

	public BadgeReader(String dataFile) {
		badges = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));

			String str;
			while ((str = br.readLine()) != null) {
				badges.add(str);
			}

			br.close();
		}catch (Exception e) {}
	}
}