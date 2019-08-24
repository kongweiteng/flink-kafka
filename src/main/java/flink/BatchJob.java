/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package flink;

import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.List;

/**
 * Skeleton for a Flink Batch Job.
 *
 * <p>For a tutorial how to write a Flink batch application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution,
 * change the main class in the POM.xml file to this class (simply search for 'mainClass')
 * and run 'mvn clean package' on the command line.
 */
public class BatchJob {

	public static void main(String[] args) throws Exception {
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

		List<Tuple3<String, String, Integer>> leftList = new ArrayList<>();
		leftList.add(new Tuple3<>("U1", "Item1", 4));
		leftList.add(new Tuple3<>("U1", "Item3", 7));
		leftList.add(new Tuple3<>("U1", "Item5", 2));
		leftList.add(new Tuple3<>("U2", "Item2", 9));
		leftList.add(new Tuple3<>("U2", "Item3", 3));
		leftList.add(new Tuple3<>("U3", "Item1", 3));

		List<Tuple1<String>> rightList = new ArrayList<>();
		rightList.add(new Tuple1<>("Item1"));
		rightList.add(new Tuple1<>("Item2"));
		rightList.add(new Tuple1<>("Item3"));
		rightList.add(new Tuple1<>("Item4"));
		rightList.add(new Tuple1<>("Item5"));

		DataSource<Tuple3<String, String, Integer>> userScoreSet = env.fromCollection(leftList);
		DataSource<Tuple1<String>> allItemSet = env.fromCollection(rightList);

		userScoreSet.print();

	}
}
