/**
 * File: BaseModel.java
 * Created: 11/8/12
 * Author: Viacheslav Panasenko
 */
package fi.spanasenko.android.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.Gson;

/**
 * BaseModel
 * Class description
 */
public class BaseModel implements Parcelable {

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        dest.writeString(json);
    }

}
