/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// scalastyle:off println
package mllib

import org.apache.spark.{SparkConf, SparkContext}

import scala.util.Random
// $example on$
import org.apache.spark.mllib.classification.{SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.BinaryClassificationMetrics
import org.apache.spark.mllib.util.MLUtils
// $example off$

/**
  *
  *
  * */
object SVMWithSGDExample {

  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setAppName("SVMWithSGDExample")
    val sc = new SparkContext(conf)
    val path =  args(0)
    val data = MLUtils.loadLibSVMFile(sc, path, args(1).toInt,args(2).toInt)
    // Split data into training (60%) and test (40%).

    val training = data.cache()

    println(training.getNumPartitions)

    // Run training algorithm to build the model
    val numIterations =  args(3).toInt

    val model = SVMWithSGD.train(training, numIterations)

    println(training.toDebugString)

    val testData = MLUtils.loadLibSVMFile(sc, args(4), args(5).toInt,args(6).toInt)
    // Clear the default threshold.
    model.clearThreshold()

    // Compute raw scores on the test set.
    val scoreAndLabels = testData.map { point =>
      val score = model.predict(point.features)
      (score, point.label)
    }

    // Get evaluation metrics.
    val metrics = new BinaryClassificationMetrics(scoreAndLabels)
    val auROC = metrics.areaUnderROC()

    println("Area under ROC = " + auROC)

//    // Save and load model
//    model.save(sc, "target/tmp/scalaSVMWithSGDModel1")
//    val sameModel = SVMModel.load(sc, "target/tmp/scalaSVMWithSGDModel1")
//    // $example off$

    sc.stop()
  }
}
// scalastyle:on println