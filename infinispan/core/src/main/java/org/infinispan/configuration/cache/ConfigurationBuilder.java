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
package org.infinispan.configuration.cache;

import static java.util.Arrays.asList;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.infinispan.config.ConfigurationException;
import org.infinispan.configuration.Builder;
import org.infinispan.configuration.ConfigurationUtils;

public class ConfigurationBuilder implements ConfigurationChildBuilder {

   private ClassLoader classLoader;
   private final ClusteringConfigurationBuilder clustering;
   private final CustomInterceptorsConfigurationBuilder customInterceptors;
   private final CustomStatsConfigurationBuilder customStats;
   private final DataContainerConfigurationBuilder dataContainer;
   private final DeadlockDetectionConfigurationBuilder deadlockDetection;
   private final EvictionConfigurationBuilder eviction;
   private final ExpirationConfigurationBuilder expiration;
   private final IndexingConfigurationBuilder indexing;
   private final InvocationBatchingConfigurationBuilder invocationBatching;
   private final JMXStatisticsConfigurationBuilder jmxStatistics;
   private final LoadersConfigurationBuilder loaders;
   private final LockingConfigurationBuilder locking;
   private final StoreAsBinaryConfigurationBuilder storeAsBinary;
   private final TransactionConfigurationBuilder transaction;
   private final VersioningConfigurationBuilder versioning;
   private final UnsafeConfigurationBuilder unsafe;
   private final List<Builder<?>> modules = new ArrayList<Builder<?>>();
   private final SitesConfigurationBuilder sites;
   private final DataPlacementConfigurationBuilder dataPlacement;
   private final GarbageCollectorConfigurationBuilder garbageCollector;

   public ConfigurationBuilder() {
      this.clustering = new ClusteringConfigurationBuilder(this);
      this.customInterceptors = new CustomInterceptorsConfigurationBuilder(this);
      this.customStats = new CustomStatsConfigurationBuilder(this);
      this.dataContainer = new DataContainerConfigurationBuilder(this);
      this.deadlockDetection = new DeadlockDetectionConfigurationBuilder(this);
      this.eviction = new EvictionConfigurationBuilder(this);
      this.expiration = new ExpirationConfigurationBuilder(this);
      this.indexing = new IndexingConfigurationBuilder(this);
      this.invocationBatching = new InvocationBatchingConfigurationBuilder(this);
      this.jmxStatistics = new JMXStatisticsConfigurationBuilder(this);
      this.loaders = new LoadersConfigurationBuilder(this);
      this.locking = new LockingConfigurationBuilder(this);
      this.storeAsBinary = new StoreAsBinaryConfigurationBuilder(this);
      this.transaction = new TransactionConfigurationBuilder(this);
      this.versioning = new VersioningConfigurationBuilder(this);
      this.unsafe = new UnsafeConfigurationBuilder(this);
      this.sites = new SitesConfigurationBuilder(this);
      this.dataPlacement = new DataPlacementConfigurationBuilder(this);
      this.garbageCollector = new GarbageCollectorConfigurationBuilder(this);
   }

   public ConfigurationBuilder classLoader(ClassLoader cl) {
      this.classLoader = cl;
      return this;
   }

   ClassLoader classLoader() {
      return classLoader;
   }

   @Override
   public ClusteringConfigurationBuilder clustering() {
      return clustering;
   }

   @Override
   public CustomInterceptorsConfigurationBuilder customInterceptors() {
      return customInterceptors;
   }

   @Override
   public DataContainerConfigurationBuilder dataContainer() {
      return dataContainer;
   }

   @Override
   public DeadlockDetectionConfigurationBuilder deadlockDetection() {
      return deadlockDetection;
   }

   @Override
   public EvictionConfigurationBuilder eviction() {
      return eviction;
   }

   @Override
   public ExpirationConfigurationBuilder expiration() {
      return expiration;
   }

   @Override
   public IndexingConfigurationBuilder indexing() {
      return indexing;
   }

   @Override
   public InvocationBatchingConfigurationBuilder invocationBatching() {
      return invocationBatching;
   }

   @Override
   public JMXStatisticsConfigurationBuilder jmxStatistics() {
      return jmxStatistics;
   }

   @Override
   public StoreAsBinaryConfigurationBuilder storeAsBinary() {
      return storeAsBinary;
   }

   @Override
   public LoadersConfigurationBuilder loaders() {
      return loaders;
   }

   @Override
   public LockingConfigurationBuilder locking() {
      return locking;
   }

   @Override
   public TransactionConfigurationBuilder transaction() {
      return transaction;
   }

   @Override
   public VersioningConfigurationBuilder versioning() {
      return versioning;
   }

   @Override
   public UnsafeConfigurationBuilder unsafe() {
      return unsafe;
   }

   public List<Builder<?>> modules() {
      return modules;
   }

   public ConfigurationBuilder clearModules() {
      modules.clear();
      return this;
   }

   public <T extends Builder<?>> T addModule(Class<T> klass) {
      try {
         Constructor<T> constructor = klass.getDeclaredConstructor(ConfigurationBuilder.class);
         T builder = constructor.newInstance(this);
         this.modules.add(builder);
         return builder;
      } catch (Exception e) {
         throw new ConfigurationException("Could not instantiate module configuration builder '" + klass.getName() + "'", e);
      }
   }

   @Override
   public SitesConfigurationBuilder sites() {
      return sites;
   }

   @Override
   public DataPlacementConfigurationBuilder dataPlacement() {
      return dataPlacement;
   }

   @Override
   public CustomStatsConfigurationBuilder customStats() {
      return customStats;
   }

   @Override
   public GarbageCollectorConfigurationBuilder garbageCollector() {
      return garbageCollector;
   }

   @SuppressWarnings("unchecked")
   public void validate() {
      for (Builder<?> validatable :
            asList(clustering, dataContainer, deadlockDetection, eviction, expiration, indexing,
                   invocationBatching, jmxStatistics, loaders, locking, storeAsBinary, transaction,
                   versioning, unsafe, sites, dataPlacement, garbageCollector, customStats)) {
         validatable.validate();
      }
      for (Builder<?> m : modules) {
         m.validate();
      }

      // TODO validate that a transport is set if a singleton store is set
   }

   @Override
   public Configuration build() {
      return build(true);
   }

   public Configuration build(boolean validate) {
      if (validate) {
         validate();
      }
      List<Object> modulesConfig = new LinkedList<Object>();
      for (Builder<?> module : modules)
         modulesConfig.add(module.create());
      return new Configuration(clustering.create(), customStats.create(), customInterceptors.create(),
                               dataContainer.create(), deadlockDetection.create(), eviction.create(),
                               expiration.create(), indexing.create(), invocationBatching.create(),
                               jmxStatistics.create(), loaders.create(), locking.create(), storeAsBinary.create(),
                               transaction.create(), unsafe.create(), versioning.create(), modulesConfig, sites.create(), classLoader,
                               dataPlacement.create(), garbageCollector.create());
   }

   public ConfigurationBuilder read(Configuration template) {
      this.classLoader = template.classLoader();
      this.clustering.read(template.clustering());
      this.customInterceptors.read(template.customInterceptors());
      this.dataContainer.read(template.dataContainer());
      this.deadlockDetection.read(template.deadlockDetection());
      this.eviction.read(template.eviction());
      this.expiration.read(template.expiration());
      this.indexing.read(template.indexing());
      this.invocationBatching.read(template.invocationBatching());
      this.jmxStatistics.read(template.jmxStatistics());
      this.loaders.read(template.loaders());
      this.locking.read(template.locking());
      this.storeAsBinary.read(template.storeAsBinary());
      this.transaction.read(template.transaction());
      this.unsafe.read(template.unsafe());
      this.sites.read(template.sites());
      this.versioning.read(template.versioning());
      this.dataPlacement.read(template.dataPlacement());
      this.garbageCollector.read(template.garbageCollector());
      this.customStats.read(template.customStatsConfiguration());

      for (Object c : template.modules().values()) {
         Builder<Object> builder = this.addModule(ConfigurationUtils.builderFor(c));
         builder.read(c);
      }

      return this;
   }

   @Override
   public String toString() {
      return "ConfigurationBuilder{" +
            "classLoader=" + classLoader +
            ", clustering=" + clustering +
            ", customInterceptors=" + customInterceptors +
            ", dataContainer=" + dataContainer +
            ", deadlockDetection=" + deadlockDetection +
            ", eviction=" + eviction +
            ", expiration=" + expiration +
            ", indexing=" + indexing +
            ", invocationBatching=" + invocationBatching +
            ", jmxStatistics=" + jmxStatistics +
            ", loaders=" + loaders +
            ", locking=" + locking +
            ", modules=" + modules +
            ", storeAsBinary=" + storeAsBinary +
            ", transaction=" + transaction +
            ", versioning=" + versioning +
            ", unsafe=" + unsafe +
            ", sites=" + sites +
            ", dataPlacement=" + dataPlacement +
            ", garbageCollector=" + garbageCollector +
            ", customStats=" + customStats +
            '}';
   }

}
