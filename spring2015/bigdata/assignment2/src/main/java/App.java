/*Zhongshan Lu
 * Assignment2
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class App {
	/*
	 * Extract the words read from file
	 */
	private static final FlatMapFunction<String, String> WORDS_EXTRACTOR = new FlatMapFunction<String, String>() {
		public Iterable<String> call(String s) throws Exception {
			String regEx = "[^a-z\u0020]";
			Iterable<String> linesIterable = Arrays.asList(s.toLowerCase()
					.replaceAll(regEx, "").split("\r"));
			List<String> resultIterable = new ArrayList<String>();
			Iterator<String> it = linesIterable.iterator();
			while (it.hasNext()) {
				String temString = it.next();
				if (temString.length() != 0) {
					resultIterable.add(temString);
				}
			}
			return resultIterable;
		}
	};
	/*
	 * Map the words
	 */
	private static final PairFunction<String, String, Integer> WORDS_MAPPER = new PairFunction<String, String, Integer>() {
		public Tuple2<String, Integer> call(String s) throws Exception {
			String words[] = s.split(" ");
			Arrays.sort(words);
			String textString = s + "\r";
			Vector<String> strs = new Vector<String>();
			Vector<Integer> numbers = new Vector<Integer>();
			int i = 0;
			int start = i;
			for (; i < (words.length - 1); i++) {
				if (!words[start].equals(words[i + 1])) {
					if (words[i].length() != 0) {
						strs.add(words[i]);
						numbers.add(i + 1 - start);
					}
					start = i + 1;
				}
			}
			strs.add(words[i]);
			numbers.add(i + 1 - start);
			for (int j = 0; j < strs.size(); j++) {
				textString = textString + "\rWith the word " + strs.get(j)
						+ ",\r";
				for (int k = 0; k < strs.size(); k++) {
					if (k != j && strs.get(k).length() != 0) {
						textString = textString + strs.get(k) + " occurs "
								+ numbers.get(k) + "\r";
					}
				}

			}
			return new Tuple2<String, Integer>(textString, 1);
		}
	};

	/*
	 * Reduce the words
	 */
	private static final Function2<Integer, Integer, Integer> WORDS_REDUCER = new Function2<Integer, Integer, Integer>() {
		public Integer call(Integer a, Integer b) throws Exception {
			return a + b;
		}
	};

	public static void main(String[] args) {

		SparkConf conf = new SparkConf().setAppName("Assignment2").setMaster(
				"local");
		JavaSparkContext context = new JavaSparkContext(conf);
		JavaRDD<String> file = context.textFile(args[0]);
		JavaRDD<String> words = file.flatMap(WORDS_EXTRACTOR);
		JavaPairRDD<String, Integer> pairs = words.mapToPair(WORDS_MAPPER);

		JavaPairRDD<String, Integer> counter = pairs.reduceByKey(WORDS_REDUCER);

		counter.saveAsTextFile(args[1]);
	}
}