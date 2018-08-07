package com.os.vee.di.module;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.os.vee.data.db.NoteDao;
import com.os.vee.data.db.VeeDb;
import com.os.vee.utils.scheduler.BaseSchedulerProvider;
import com.os.vee.utils.scheduler.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Omar on 13-Jul-18 11:58 PM.
 */

@Module(includes = {ViewModelModule.class})
public class AppModule {

    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public FirebaseFirestore provideFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        );
        return firestore;
    }

    @Provides
    @Singleton
    public VeeDb provideDb(Application context) {
        return Room.databaseBuilder(context, VeeDb.class, "veeDb").build();
    }

    @Provides
    @Singleton
    public NoteDao provideNoteDao(VeeDb db) {
        return db.noteDao();
    }

    @Provides
    @Singleton
    public BaseSchedulerProvider schedulerProvider() {
        return new SchedulerProvider();
    }
}
