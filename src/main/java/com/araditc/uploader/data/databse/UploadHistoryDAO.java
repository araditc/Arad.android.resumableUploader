package com.araditc.uploader.data.databse;

import androidx.lifecycle.MutableLiveData;

import com.araditc.uploader.AppConfig;
import com.araditc.uploader.struct.UploadHistoryDbStruct;
import com.araditc.uploader.struct.UploadHistoryStruct;
import com.araditc.uploader.struct.UploadStruct;
import com.araditc.uploader.utils.AppUtil;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class UploadHistoryDAO {
    private Realm realm;
    private final String FILE_ID_COLUMN = "fileId";
    private final String ID_COLUMN = "id";
    private final String SEQUENCE_NUMBER_COLUMN = "totalSequenceNumber";
    private final String STATUS_COLUMN = "status";
    private UploadHistoryStruct uploadHistoryStruct;
    List<UploadHistoryStruct> uploadStructList;
    private static final MutableLiveData<List<UploadHistoryStruct>> uploadHistoryMutableLiveData=new MutableLiveData<>();

    public UploadHistoryDAO() {
        AppConfig.newInstance().configRealm(AppConfig.application);
        this.realm = AppConfig.farmRealm;
    }

    private void syncMutableList() {
        List<UploadHistoryStruct> uploadHistoryStructList = UploadHistoryStruct.convert(findAll());
        AppUtil.runOnUIThread(() -> uploadHistoryMutableLiveData.setValue(uploadHistoryStructList),0);
    }

    public void save(UploadHistoryDbStruct uploadHistoryDbStruct, TransActionResult transActionResult) {

        realm.executeTransaction(realm -> {
            UploadHistoryDbStruct existHistoryDbStruct = realm.where(UploadHistoryDbStruct.class)
                    .equalTo(FILE_ID_COLUMN, uploadHistoryDbStruct.getFileId()).findFirst();

            if (existHistoryDbStruct == null) {
                Number currentIdNum = realm.where(UploadHistoryDbStruct.class).max(ID_COLUMN);
                int nextId;

                if (currentIdNum == null) nextId = 1;
                else nextId = currentIdNum.intValue() + 1;

                UploadHistoryDbStruct realmObj = realm.createObject(UploadHistoryDbStruct.class, nextId);
                realmObj.init(uploadHistoryDbStruct);

                uploadHistoryDbStruct.setId(nextId);

                syncMutableList();

                if (transActionResult == null) return;

                UploadHistoryStruct uploadHistoryStruct = new UploadHistoryStruct();
                uploadHistoryStruct.convert(uploadHistoryDbStruct);
                transActionResult.onSuccess(uploadHistoryStruct);


            } else {
                existHistoryDbStruct.setStatus(uploadHistoryDbStruct.getStatus());

                if (transActionResult == null) return;

                UploadHistoryStruct uploadHistoryStruct = new UploadHistoryStruct();
                uploadHistoryStruct.convert(uploadHistoryDbStruct);
                transActionResult.onSuccess(uploadHistoryStruct);
            }
        });
    }

    public void delete(UploadStruct uploadStruct, TransActionResult transActionResult) {

        realm.executeTransactionAsync(realm -> {
            UploadHistoryDbStruct uploadHistoryDbStruct = realm.where(UploadHistoryDbStruct.class)
                    .equalTo(ID_COLUMN, uploadStruct.getId()).findFirst();

            uploadHistoryDbStruct.deleteFromRealm();
        }, () -> {

            if (transActionResult == null) return;

            transActionResult.onSuccess(uploadHistoryStruct);
            syncMutableList();

        }, error -> {
            if (transActionResult == null) return;

            transActionResult.onError(error);
        });
    }

    public void update(String fileId, int status, TransActionResult transActionResult) {
        realm.executeTransaction(realm -> {
            UploadHistoryDbStruct uploadHistoryDbStruct = realm.where(UploadHistoryDbStruct.class)
                    .equalTo(FILE_ID_COLUMN, fileId).findFirst();

            uploadHistoryDbStruct.setFileId(fileId);
            uploadHistoryDbStruct.setStatus(status);

            uploadHistoryStruct = new UploadHistoryStruct();
            uploadHistoryStruct.convert(uploadHistoryDbStruct);

            syncMutableList();

            if (transActionResult == null) return;

            transActionResult.onSuccess(uploadHistoryStruct);

        });
    }

    public void find(int id, TransActionResult transActionResult) {
        realm.executeTransactionAsync(realm -> {
            UploadHistoryDbStruct uploadHistoryDbStruct = realm.where(UploadHistoryDbStruct.class)
                    .equalTo(ID_COLUMN, id).findFirst();

            uploadHistoryStruct = new UploadHistoryStruct();
            uploadHistoryStruct.convert(uploadHistoryDbStruct);

        }, () -> {

            if (transActionResult == null) return;

            transActionResult.onSuccess(uploadHistoryStruct);

        }, error -> {
            if (transActionResult == null) return;

            transActionResult.onError(error);
        });
    }

    public void findByFileId(String uploadId, TransActionResult transActionResult) {
        realm.executeTransactionAsync(realm -> {
            UploadHistoryDbStruct uploadHistoryDbStruct = realm.where(UploadHistoryDbStruct.class)
                    .equalTo(FILE_ID_COLUMN, uploadId).findFirst();

            uploadHistoryStruct = new UploadHistoryStruct();
            uploadHistoryStruct.convert(uploadHistoryDbStruct);

        }, () -> {

            if (transActionResult == null) return;

            transActionResult.onSuccess(uploadHistoryStruct);

        }, error -> {
            if (transActionResult == null) return;

            transActionResult.onError(error);
        });
    }

    public MutableLiveData<List<UploadHistoryStruct>> getUploadHistoryMutableLiveData() {
        return uploadHistoryMutableLiveData;
    }

    public RealmResults<UploadHistoryDbStruct> findAll() {
        return realm.where(UploadHistoryDbStruct.class).findAll();
    }

    public void close() {
        if (realm != null)
            realm.close();
    }

    public interface TransActionResult {
        void onSuccess(UploadHistoryStruct uploadHistoryStruct);

        void onError(Throwable error);
    }

    public interface TransActionListResult {
        void onSuccess(List<UploadHistoryStruct> structList);

        void onError(Throwable error);
    }
}
