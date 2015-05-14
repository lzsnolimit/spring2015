package project4.project4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import jodd.util.collection.SortedArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Twitter {
	public static List<String> lists = new SortedArrayList<String>();
	public static Map<Integer, String> pairs = new TreeMap<Integer, String>(
			new Comparator<Integer>() {

				public int compare(Integer o1, Integer o2) {
					// TODO Auto-generated method stub
					return o2 - o1;
				}
			});

	public static class TokenizerMapper extends
			Mapper<Object, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] pair = value.toString().split(",");
			Hbase.addData(pair[0], "content", new String(pair[1].getBytes(),
					"UTF-8"));

			String regEx = "[^a-z0-9\u0020]";
			StringTokenizer itr = new StringTokenizer(value.toString()
					.replaceAll(regEx, " "));
			while (itr.hasMoreTokens()) {
				word.set(itr.nextToken());
				context.write(word, one);
			}
			// System.out.println(line);
		}
	}

	public static class IntSumReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {

			if (!lists.contains(key.toString())) {
				int sum = 0;
				for (IntWritable val : values) {
					sum += val.get();
				}

				pairs.put(sum, key.toString());
				result.set(sum);
				context.write(key, result);
			}

		}
	}

	public static void readStopWords() {
		File file = new File("stop-word-list.txt");
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(file));
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				lists.add(temp);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws Exception {
		Hbase.setStrings("twitter", "text");
		Hbase.createTable();
		readStopWords();
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "word count");
		job.setJarByClass(Twitter.class);
		job.setMapperClass(TokenizerMapper.class);
		job.setCombinerClass(IntSumReducer.class);
		job.setReducerClass(IntSumReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path("input"));
		FileOutputFormat.setOutputPath(job, new Path("output"));

		job.waitForCompletion(true);
		//Hbase.getAllData();
		Iterator it = pairs.entrySet().iterator();
		int count = 0;
		Hbase.setStrings("result", "frequency");
		Hbase.createTable();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Integer key = (Integer) entry.getKey();
			String value = (String) entry.getValue();
			Hbase.addData(String.valueOf(count), value, String.valueOf(key));
			count++;
			if (count == 20) {
				break;
			}
		}
		System.out.println("Here is the result table");
		Hbase.getAllData();
	}
}