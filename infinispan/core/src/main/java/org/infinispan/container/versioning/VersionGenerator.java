/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA
 */

package org.infinispan.container.versioning;

import org.infinispan.topology.CacheTopology;

import java.util.List;

/**
 * Generates versions
 *
 * @author Manik Surtani
 * @since 5.1
 */
public interface VersionGenerator {
   /**
    * Generates a new entry version
    * @return a new entry version
    */
   IncrementableEntryVersion generateNew();

   IncrementableEntryVersion increment(IncrementableEntryVersion initialVersion);

   void updateCacheTopology(List<CacheTopology> cacheTopologyList);

   void addCacheTopology(CacheTopology cacheTopology);

   void gcTopologyIds(int minTopologyId);

   int getViewHistorySize();
}
