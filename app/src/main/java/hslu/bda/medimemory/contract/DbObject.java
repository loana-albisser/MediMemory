package hslu.bda.medimemory.contract;

import android.content.ContentValues;

import java.io.Serializable;

/**
 * Created by tbeugste on 11.03.2016.
 */
public interface DbObject {
    public ContentValues getContentValues();
    public String getTableName();
    public String getPrimaryFieldName();
    public String getPrimaryFieldValue();
    public void setId(int id);
    public boolean isChanged();

}
