/*
 * Copyright (c) 2011-2018, Meituan Dianping. All Rights Reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dianping.cat.report.page.event.transform;

import com.dianping.cat.Constants;
import com.dianping.cat.consumer.event.EventReportMerger;
import com.dianping.cat.consumer.event.model.entity.EventName;
import com.dianping.cat.consumer.event.model.entity.EventReport;
import com.dianping.cat.consumer.event.model.entity.EventType;
import com.dianping.cat.consumer.event.model.entity.GraphTrend;
import com.dianping.cat.consumer.event.model.entity.Machine;
import com.dianping.cat.consumer.event.model.entity.Range;
import com.dianping.cat.consumer.event.model.transform.BaseVisitor;

public class AllMachineMerger extends BaseVisitor {

	public EventReport m_report;

	public String m_currentType = null;

	public String m_currentName = null;

	public Integer m_currentRange;

	public EventReportMerger m_merger = new EventReportMerger(new EventReport());

	public EventReport getReport() {
		return m_report;
	}

	@Override
	public void visitEventReport(EventReport eventReport) {
		if (eventReport == null) {
			return;
		}
		m_report = new EventReport(eventReport.getDomain());
		m_report.setStartTime(eventReport.getStartTime());
		m_report.setEndTime(eventReport.getEndTime());
		m_report.getIps().addAll(eventReport.getIps());

		super.visitEventReport(eventReport);
	}

	@Override
	public void visitMachine(Machine machine) {
		m_report.findOrCreateMachine(Constants.ALL);
		super.visitMachine(machine);
	}

	@Override
	public void visitName(EventName name) {
		m_currentName = name.getId();
		EventName temp = m_report.findOrCreateMachine(Constants.ALL).findOrCreateType(m_currentType)
								.findOrCreateName(m_currentName);

		m_merger.mergeName(temp, name);
		super.visitName(name);
		m_currentName = null;
	}

	@Override
	public void visitRange(Range range) {
		m_currentRange = range.getValue();
		Range temp = m_report.findOrCreateMachine(Constants.ALL).findOrCreateType(m_currentType)
								.findOrCreateName(m_currentName).findOrCreateRange(m_currentRange);

		m_merger.mergeRange(temp, range);
		super.visitRange(range);
	}

	@Override
	public void visitType(EventType type) {
		m_currentType = type.getId();
		EventType temp = m_report.findOrCreateMachine(Constants.ALL).findOrCreateType(m_currentType);

		m_merger.mergeType(temp, type);
		super.visitType(type);
		m_currentType = null;
	}

	@Override
	public void visitGraphTrend(GraphTrend graph) {
		GraphTrend temp = null;

		if (m_currentName != null) {
			EventName name = m_report.findOrCreateMachine(Constants.ALL).findOrCreateType(m_currentType)
									.findOrCreateName(m_currentName);
			temp = name.getGraphTrend();

			if (temp == null) {
				temp = new GraphTrend();
				temp.setDuration(graph.getDuration());
				name.setGraphTrend(temp);
			}
		} else {
			EventType type = m_report.findOrCreateMachine(Constants.ALL).findOrCreateType(m_currentType);
			temp = type.getGraphTrend();

			if (temp == null) {
				temp = new GraphTrend();
				temp.setDuration(graph.getDuration());
				type.setGraphTrend(temp);
			}
		}

		m_merger.mergeGraphTrend(temp, graph);
		super.visitGraphTrend(graph);
	}
}
