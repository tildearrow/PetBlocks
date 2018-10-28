package com.github.shynixn.petblocks.bukkit

import com.github.shynixn.petblocks.api.business.commandexecutor.EditPetCommandExecutor
import com.github.shynixn.petblocks.api.business.commandexecutor.PlayerPetActionCommandExecutor
import com.github.shynixn.petblocks.api.business.commandexecutor.ReloadCommandExecutor
import com.github.shynixn.petblocks.api.business.enumeration.PluginDependency
import com.github.shynixn.petblocks.api.business.enumeration.Version
import com.github.shynixn.petblocks.api.business.proxy.SqlConnectionPoolProxy
import com.github.shynixn.petblocks.api.business.service.*
import com.github.shynixn.petblocks.api.persistence.context.SqlDbContext
import com.github.shynixn.petblocks.api.persistence.repository.PetMetaRepository
import com.github.shynixn.petblocks.api.persistence.repository.PetRepository
import com.github.shynixn.petblocks.bukkit.logic.business.extension.toVersion
import com.github.shynixn.petblocks.bukkit.logic.business.nms.VersionSupport
import com.github.shynixn.petblocks.bukkit.logic.business.proxy.SqlProxyImpl
import com.github.shynixn.petblocks.bukkit.logic.business.service.*
import com.github.shynixn.petblocks.core.logic.business.commandexecutor.EditPetCommandExecutorImpl
import com.github.shynixn.petblocks.core.logic.business.commandexecutor.PlayerPetActionCommandExecutorImpl
import com.github.shynixn.petblocks.core.logic.business.commandexecutor.ReloadCommandExecutorImpl
import com.github.shynixn.petblocks.core.logic.business.service.*
import com.github.shynixn.petblocks.core.logic.persistence.context.SqlDbContextImpl
import com.github.shynixn.petblocks.core.logic.persistence.repository.PetMetaSqlRepository
import com.github.shynixn.petblocks.core.logic.persistence.repository.PetRunTimeRepository
import com.google.inject.AbstractModule
import com.google.inject.Scopes
import org.bukkit.plugin.Plugin

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class PetBlocksDependencyInjectionBinder(private val plugin: Plugin) : AbstractModule() {
    /**
     * Configures the business logic tree.
     */
    override fun configure() {
        val versionSupport = VersionSupport.getServerVersion()

        bind(Plugin::class.java).toInstance(plugin)
        bind(Version::class.java).toInstance(versionSupport.toVersion())

        // CommandExecutors
        bind(ReloadCommandExecutor::class.java).to(ReloadCommandExecutorImpl::class.java)
        bind(PlayerPetActionCommandExecutor::class.java).to(PlayerPetActionCommandExecutorImpl::class.java)
        bind(EditPetCommandExecutor::class.java).to(EditPetCommandExecutorImpl::class.java)

        // Services
        bind(LoggingService::class.java).toInstance(LoggingUtilServiceImpl(plugin.logger))
        bind(ParticleService::class.java).to(ParticleServiceImpl::class.java)
        bind(SoundService::class.java).to(SoundServiceImpl::class.java)
        bind(GUIScriptService::class.java).to(GUIScriptServiceImpl::class.java)
        bind(UpdateCheckService::class.java).to(UpdateCheckServiceImpl::class.java)
        bind(MessageService::class.java).to(MessageServiceImpl::class.java)
        bind(PetActionService::class.java).to(PetActionServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ProxyService::class.java).to(ProxyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ItemService::class.java).to(ItemServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(EntityService::class.java).to(EntityServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ConcurrencyService::class.java).to(ConcurrencyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(CommandService::class.java).to(CommandServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PersistencePetMetaService::class.java).to(PersistencePetMetaServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(ConfigurationService::class.java).to(ConfigurationServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(GUIService::class.java).to(GUIServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(FeedingPetService::class.java).to(FeedPetServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(PetService::class.java).to(PetServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(DependencyService::class.java).to(DependencyServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(CarryPetService::class.java).to(CarryPetServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(CombatPetService::class.java).to(CombatPetServiceImpl::class.java).`in`(Scopes.SINGLETON)
        bind(DependencyHeadDatabaseService::class.java).to(DependencyHeadDatabaseServiceImpl::class.java).`in`(Scopes.SINGLETON)

        // Context
        bind(SqlConnectionPoolProxy::class.java).to(SqlProxyImpl::class.java).`in`(Scopes.SINGLETON)
        bind(SqlDbContext::class.java).to(SqlDbContextImpl::class.java).`in`(Scopes.SINGLETON)

        // Repository
        bind(PetMetaRepository::class.java).to(PetMetaSqlRepository::class.java).`in`(Scopes.SINGLETON)
        bind(PetRepository::class.java).to(PetRunTimeRepository::class.java).`in`(Scopes.SINGLETON)

        when {
            versionSupport.isVersionSameOrGreaterThan(VersionSupport.VERSION_1_13_R2) -> bind(EntityRegistrationService::class.java).to(EntityRegistration113R2ServiceImpl::class.java).`in`(Scopes.SINGLETON)
            versionSupport.isVersionSameOrGreaterThan(VersionSupport.VERSION_1_13_R1) -> bind(EntityRegistrationService::class.java).to(EntityRegistration113R1ServiceImpl::class.java).`in`(Scopes.SINGLETON)
            versionSupport.isVersionSameOrGreaterThan(VersionSupport.VERSION_1_11_R1) -> bind(EntityRegistrationService::class.java).to(EntityRegistration111R1ServiceImpl::class.java).`in`(Scopes.SINGLETON)
            else -> bind(EntityRegistrationService::class.java).to(EntityRegistration18R1ServiceImpl::class.java).`in`(Scopes.SINGLETON)
        }

        // Dependency resolving
        val dependencyService = DependencyServiceImpl(plugin)
        dependencyService.checkForInstalledDependencies()

        if (dependencyService.isInstalled(PluginDependency.WORLDGUARD)) {
            val version = dependencyService.getVersion(PluginDependency.WORLDGUARD)

            if (version.startsWith("5")) {
                bind(DependencyWorldGuardService::class.java).to(DependencyWorldGuard5Impl::class.java).`in`(Scopes.SINGLETON)
            } else {
                bind(DependencyWorldGuardService::class.java).to(DependencyWorldGuard6Impl::class.java).`in`(Scopes.SINGLETON)
            }
        }
    }
}