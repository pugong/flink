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

package org.apache.flink.api.table.plan.rules.dataSet

import org.apache.calcite.plan.{Convention, RelOptRule, RelOptRuleCall, RelTraitSet}
import org.apache.calcite.rel.RelNode
import org.apache.calcite.rel.convert.ConverterRule
import org.apache.calcite.rel.logical.LogicalMinus
import org.apache.calcite.rel.rules.UnionToDistinctRule
import org.apache.flink.api.table.plan.nodes.dataset.{DataSetConvention, DataSetMinus}

class DataSetMinusRule
  extends ConverterRule(
    classOf[LogicalMinus],
    Convention.NONE,
    DataSetConvention.INSTANCE,
    "DataSetMinusRule")
{

  def convert(rel: RelNode): RelNode = {

    val minus: LogicalMinus = rel.asInstanceOf[LogicalMinus]
    val traitSet: RelTraitSet = rel.getTraitSet.replace(DataSetConvention.INSTANCE)
    val convLeft: RelNode = RelOptRule.convert(minus.getInput(0), DataSetConvention.INSTANCE)
    val convRight: RelNode = RelOptRule.convert(minus.getInput(1), DataSetConvention.INSTANCE)

    new DataSetMinus(
      rel.getCluster,
      traitSet,
      convLeft,
      convRight,
      rel.getRowType,
      minus.all)
  }
}

object DataSetMinusRule {
  val INSTANCE: RelOptRule = new DataSetMinusRule
}

