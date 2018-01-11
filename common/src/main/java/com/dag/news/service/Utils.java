package com.dag.news.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

//import org.apache.commons.math3.ml.clustering.CentroidCluster;
//import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.NumberUtils;
import org.tartarus.snowball.SnowballStemmer;

import com.dag.news.model.Language;

public class Utils {

	static private Logger logger = LoggerFactory.getLogger(Utils.class);

	static public boolean isNumber(String t, Language l) {
		if (!t.matches("[0-9\\.,]+"))
			return false;

		Locale loc = null;
		try {
			loc = new Locale(l.getName());
		} catch (Exception ex) {
			logger.debug("invalid locale [" + l.getName() + "]");
		}
		try {
			if (loc != null) {
				NumberUtils.parseNumber(t, BigDecimal.class, NumberFormat.getNumberInstance(loc));
			} else {
				NumberUtils.parseNumber(t, BigDecimal.class, NumberFormat.getNumberInstance(loc));
			}
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	static public List<String> getWords(String title, Language language, Set<String> stopWords, boolean stemm) {
		List<String> res = new ArrayList<String>();

		if (stemm) {
			String[] w = title.split("(\\p{Z}|\\p{P}|\\||\\+)+");

			for (int i = 0; i < w.length; i++) {
				String tt = w[i];
				tt = tt.trim().toLowerCase();

				if (tt.length() > 0) {
					if (tt.matches("[^0-9]*[0-9]+[^0-9]*")) {
						// TODO if is number return semantic number???
						continue;
					}

					if (stopWords.contains(tt))
						continue;

					res.add(stemm ? stemming(tt, language) : tt);
				}
			}
		} else {

			String[] w = title.split("(\\p{Z}|[;:\\|\\\\/])+");

			for (int i = 0; i < w.length; i++) {
				String tt = w[i];
				while (tt.length() >= 1 && tt.substring(0, 1).matches("[\\p{P}&&[^#@]]|«|»|“|”|‘|’|´|\\?|¿|!|¡")) {
					tt = tt.substring(1);
				}

				while (tt.length() > 1
						&& tt.substring(tt.length() - 1, tt.length()).matches("\\p{P}|«|»|“|”|‘|’|´|\\?|¿|!|¡")) {
					tt = tt.substring(0, tt.length() - 1);
				}

				tt = tt.trim().toLowerCase();

				if (tt.length() > 0) {
					if (isNumber(tt, language)) {
						// TODO if is number return semantic number???
						continue;
					}

					if (stopWords.contains(tt))
						continue;

					res.add(stemm ? stemming(tt, language) : tt);
				}
			}
		}
		return res;
	}

	static public String stemming(String w, Language language) {
		// http://snowball.tartarus.org/

		SnowballStemmer uu = null;
		switch (language.getName()) {
		case "es":
		case "es-es":
			uu = new org.tartarus.snowball.ext.spanishStemmer();
			break;
		case "en":
		case "en-gb":
		case "en-uk":
		case "en-us":
			uu = new org.tartarus.snowball.ext.englishStemmer();
			break;
		}

		if (uu != null) {
			uu.setCurrent(w);
			if (uu.stem())
				return uu.getCurrent();
		}
		return w;
	}

	//
	//
	// //
	// // static double cosine(double[]vector1, double[]vector2) {
	// // """ related documents j and q are in the concept space by comparing
	// the
	// // vectors :
	// // cosine = ( V1 * V2 ) / ||V1|| x ||V2|| """
	// // return float(Utils dot(vector1,vector2) / (norm(vector1) *
	// // norm(vector2)))
	// // }
	// //
	// public static void main(String title[]) {
	// try {
	// File file = new File(
	// "C:/Users/David/Documents/Visual Studio
	// 2015/Projects/NewsFromBing/java/news/src/main/resources/data.txt");
	//
	// BufferedReader reader = new BufferedReader(new InputStreamReader(new
	// FileInputStream(file)));
	//
	// List<String> titles = new ArrayList<String>();
	// // List<List<String>> titlesL = new ArrayList<List<String>>();
	//
	// Map<String, Integer> frequency = new HashMap<String, Integer>();
	// Map<String, Integer> ocurrences = new HashMap<String, Integer>();
	//
	// Set<String> words = new HashSet<String>();
	// String line = null;
	// while ((line = reader.readLine()) != null) {
	//
	// List<String> wordsOnLIne = getWords(line, null);
	//
	// Set<String> cn = new HashSet<String>();
	// for (String wo : wordsOnLIne) {
	// {
	// Integer count = frequency.get(wo);
	// if (count == null) {
	// count = 1;
	// } else {
	// count++;
	// }
	// frequency.put(wo, count);
	// }
	// if (!cn.contains(wo)) {
	// Integer count = ocurrences.get(wo);
	// if (count == null) {
	// count = 1;
	// } else {
	// count++;
	// }
	// ocurrences.put(wo, count);
	//
	// cn.add(wo);
	// }
	// }
	//
	// // titlesL.add(wordsOnLIne);
	// titles.add(line);
	//
	// words.addAll(wordsOnLIne);
	//
	// logger.info("++ " + line + " >> " + wordsOnLIne.size() + "/" +
	// words.size());
	// }
	//
	// Vector<String> vector = new Vector<String>(words.size());
	// vector.addAll(words);
	// Collections.sort(vector);
	//
	// Vector<Sentence> info = new Vector<Sentence>(titles.size());
	//
	// // BlockRealMatrix matrix = new BlockRealMatrix(titles.size(),
	// // vector.size());
	//
	// for (int i = 0, N = titles.size(); i < N; i++) {
	// double[] values = new double[vector.size()];
	// Arrays.fill(values, 0.0);
	// for (String w : getWords(titles.get(i), null)) {
	//
	// int n = Collections.binarySearch(vector, w);
	// values[n] += 1; // frequency.get(w); // kkkkkkkk
	//
	// }
	// // matrix.setRow(i, values);
	//
	// info.add(new Sentence(titles.get(i), values));
	// }
	//
	// for (int i = 0, N = info.size(); i < N; i++) {
	// Sentence detail = info.get(i);
	// double wordTotalCount = detail.total();
	//
	// for (int j = 0, M = detail.points.length; j < M; j++) {
	// String w = vector.get(j);
	// // double freq = frequency.get(w);
	// double wordOccurence = ocurrences.get(w);
	// // double wordOccurence = 0.0;
	// // for (int i = 0, N = info.size(); i < N; i++) {
	// // Sentence detail = info.get(i);
	//
	// if (detail.points[j] != 0.0) {
	// detail.points[j] = (detail.points[j] / wordTotalCount)
	// * Math.log(Math.abs((double) N / wordOccurence));
	// }
	// }
	// }
	// /*
	// * wordTotal= reduce(lambda x, y: x+y, self.matrix[row] )
	// *
	// * for col in xrange(0,cols): #For each term
	// *
	// * #For consistency ensure all self.matrix values are floats
	// * self.matrix[row][col] = float(self.matrix[row][col])
	// *
	// * if self.matrix[row][col]!=0:
	// *
	// * termDocumentOccurences = self.__getTermDocumentOccurences(col)
	// *
	// * termFrequency = self.matrix[row][col] / float(wordTotal)
	// * inverseDocumentFrequency = log(abs(documentTotal /
	// * float(termDocumentOccurences)))
	// * self.matrix[row][col]=termFrequency*inverseDocumentFrequency
	// */
	//
	// // int x = (int) Math.round(Math.random() * titles.size());
	// // RealVector vec = matrix.getRowVector(x);
	// // // double[] res = new double[titles.size()];
	// // for (int i = 0, N = titles.size(); i < N; i++) {
	// // if (i == x) continue;
	// //
	// // logger.info (">>>>>>>>>>>>>processing " + i);
	// //
	// // RealVector voc = matrix.getRowVector(i);
	// //
	// // if (voc.getL1Norm() > 0) {
	// // info.get(i).cousine = voc.cosine(vec);
	// // }
	// // else {
	// // logger.info (">>>>>>>>>>>>>sckip " + i);
	// // }
	// // // def search(self,searchList):
	// // // """ search for documents that match based on a list of terms
	// // // """
	// // // queryVector = self.buildQueryVector(searchList)
	// // //
	// // // ratings = [util.cosine(queryVector, documentVector) for
	// // // documentVector in self.documentVectors]
	// // // ratings.sort(reverse=True)
	// // // return ratings
	// // }
	// //
	// // Collections.sort(info, new Comparator<Sentence>() {
	// //
	// // @Override
	// // public int compare(Sentence o1, Sentence o2) {
	// // return (int) Math.signum(o2.cousine - o1.cousine);
	// // }
	// //
	// // });
	// //
	// // info.get(0);
	//
	// KMeansPlusPlusClusterer<Sentence> clusterer = new
	// KMeansPlusPlusClusterer<Sentence>(titles.size() / 10,
	// 50000);
	// List<CentroidCluster<Sentence>> clusterResults = clusterer.cluster(info);
	//
	// // output the clusters
	// PrintStream out = new PrintStream(new File("resultado.txt"));
	// for (int i = 0; i < clusterResults.size(); i++) {
	// out.println("Cluster " + i);
	// for (Sentence locationWrapper : clusterResults.get(i).getPoints()) {
	// out.println("\t" + locationWrapper.title);
	// }
	// out.println();
	// }
	// out.close();
	//
	// } catch (
	//
	// Exception ex) {
	// logger.error("error", ex);
	// }
	// }
}
