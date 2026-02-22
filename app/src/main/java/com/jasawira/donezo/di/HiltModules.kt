package com.jasawira.donezo.di

import android.content.Context
import com.jasawira.donezo.data.local.database.AppDatabase
import com.jasawira.donezo.data.local.dao.CardDao
import com.jasawira.donezo.data.local.dao.CategoryDao
import com.jasawira.donezo.data.local.dao.ChecklistItemDao
import com.jasawira.donezo.data.repository.CardRepositoryImpl
import com.jasawira.donezo.data.repository.CategoryRepositoryImpl
import com.jasawira.donezo.data.repository.ChecklistRepositoryImpl
import com.jasawira.donezo.data.repository.SearchRepositoryImpl
import com.jasawira.donezo.domain.repository.CardRepository
import com.jasawira.donezo.domain.repository.CategoryRepository
import com.jasawira.donezo.domain.repository.ChecklistRepository
import com.jasawira.donezo.domain.repository.SearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DATABASE MODULE
 * Menyediakan AppDatabase sebagai singleton
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    /**
     * Provide CategoryDao dari AppDatabase
     */
    @Provides
    @Singleton
    fun provideCategoryDao(database: AppDatabase): CategoryDao {
        return database.categoryDao()
    }

    /**
     * Provide CardDao dari AppDatabase
     */
    @Provides
    @Singleton
    fun provideCardDao(database: AppDatabase): CardDao {
        return database.cardDao()
    }

    /**
     * Provide ChecklistItemDao dari AppDatabase
     */
    @Provides
    @Singleton
    fun provideChecklistItemDao(database: AppDatabase): ChecklistItemDao {
        return database.checklistItemDao()
    }
}

/**
 * REPOSITORY MODULE
 * Menyediakan repository implementations dengan interface mereka
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: CategoryDao
    ): CategoryRepository {
        return CategoryRepositoryImpl(categoryDao)
    }

    @Provides
    @Singleton
    fun provideCardRepository(
        cardDao: CardDao,
        checklistItemDao: ChecklistItemDao
    ): CardRepository {
        return CardRepositoryImpl(cardDao, checklistItemDao)
    }

    @Provides
    @Singleton
    fun provideChecklistRepository(
        checklistItemDao: ChecklistItemDao
    ): ChecklistRepository {
        return ChecklistRepositoryImpl(checklistItemDao)
    }

    @Provides
    @Singleton
    fun provideSearchRepository(
        cardDao: CardDao,
        checklistItemDao: ChecklistItemDao
    ): SearchRepository {
        return SearchRepositoryImpl(cardDao, checklistItemDao)
    }
}

/**
 * APP MODULE
 * Module umum untuk app-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context {
        return context
    }
}

