package com.test.hazelcast.jet.wordcount;

import java.util.List;
import java.util.Map;

import com.hazelcast.jet.Jet;
import com.hazelcast.jet.JetInstance;
import com.hazelcast.jet.Traversers;
import com.hazelcast.jet.aggregate.AggregateOperations;
import com.hazelcast.jet.function.DistributedFunctions;
import com.hazelcast.jet.pipeline.Pipeline;
import com.hazelcast.jet.pipeline.Sinks;
import com.hazelcast.jet.pipeline.Sources;

public class WordCount {
	public static void main(String[] args) throws Exception {

		Pipeline p = Pipeline.create();
		p.drawFrom(Sources.<String>list("text"))
			.flatMap(word -> Traversers.traverseArray(word.toLowerCase().split("\\W+")))
			.filter(word -> !word.isEmpty())
			.groupingKey(DistributedFunctions.wholeItem())
			.aggregate(AggregateOperations.counting())
			.drainTo(Sinks.map("counts"));

		// Start Jet, populate the input list
		JetInstance jet = Jet.newJetInstance();
		try {
			List<String> text = jet.getList("text");
			text.add("Mil Pat Mil Pat Mil");
			text.add("Pat Pat Pat Mil");

			// Perform the computation
			jet.newJob(p).join();

			// Check the results
			Map<String, Long> counts = jet.getMap("counts");
			System.out.println("Count of Mil: " + counts.get("mil"));
			System.out.println("Count of Pat: " + counts.get("pat"));
		} finally {
			Jet.shutdownAll();
		}

	}
}
