package hslu.bda.medimemory.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tbeugste on 02.03.2016.
 */
public class DbHelper extends SQLiteOpenHelper{
    public static String DB_NAME = "MediMemory";
    public static int DB_Version = 1;

    public static String TABLE_MEDI_EAT = "Medi_eat";
    public static String TABLE_MEDI_DATA = "Medi_data";
    public static String TABLE_MEDI_STATUS = "Medi_status";
    public static String TABLE_MEDI_CONSUMED = "Medi_consumed";
    public static String TABLE_MEDI_DAY = "Medi_day";
    public static String TABLE_MEDI_CONSINDIV = "Medi_consumeIndividual";
    public static String TABLE_MEDI_CONSINTER = "Medi_consumeInterval";

    public static String COLUMN_ID = "ID";
    public static String COLUMN_DESC = "description";
    public static String COLUMN_DURATION = "duration";
    public static String COLUMN_CONSTIME = "consumeTime";
    public static String COLUMN_POINTINTIME = "pointInTime";
    public static String COLUMN_EATPART = "eatpart";
    public static String COLUMN_AMOUNT = "amount";
    public static String COLUMN_WIDTH = "width";
    public static String COLUMN_LENGTH = "length";
    public static String COLUMN_PICTURE = "picture";
    public static String COLUMN_ACTIVE = "active";
    public static String COLUMN_MEDIID = "MediID";
    public static String COLUMN_CONSDAY = "consumeDay";
    public static String COLUMN_STATUS = "status";
    public static String COLUMN_DAYPART = "daypart";
    public static String COLUMN_STARTTIME = "startTime";
    public static String COLUMN_ENDTIME = "endTime";
    public static String COLUMN_INTERVAL = "interval";
    public static String COLUMN_WEEKDAY = "weekday";
    public static String COLUMN_CREATEDATE="createDate";
    public static String COLUMN_NOTE = "note";

    public DbHelper(final Context context) {
        super(context, DB_NAME, null, DB_Version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createDayTable(db);
        createEatTable(db);
        createDataTable(db);
        createConsumeIndividualTable(db);
        createConsumeIntervalTable(db);
        createStatusTable(db);
        createConsumeTable(db);
    }

    private void createDayTable(SQLiteDatabase db){
        String CREATE_DAY_TABLE = "CREATE TABLE "+this.TABLE_MEDI_DAY + " (" +
                this.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                this.COLUMN_DESC + " INTEGER);";
        db.execSQL(CREATE_DAY_TABLE);

        String CREATE_INIT_VALUES = "INSERT INTO "+this.TABLE_MEDI_DAY + " "+
                "Select 0, 'vorher'"+
                "UNION SELECT 1, 'Morgens'"+
                "UNION SELECT 2, 'Mittags'"+
                "UNION SELECT 3, 'Abends'" +
                "UNION SELECT 4, 'Nachts';";
        db.execSQL(CREATE_INIT_VALUES);
    }

    private void createEatTable(SQLiteDatabase db) {
        String CREATE_EAT_TABLE = "CREATE TABLE "+this.TABLE_MEDI_EAT + " (" +
                this.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                this.COLUMN_DESC + " INTEGER);";
        db.execSQL(CREATE_EAT_TABLE);

        String CREATE_INIT_VALUES = "INSERT INTO "+this.TABLE_MEDI_EAT + " "+
                "Select 0, 'vorher'"+
                "UNION SELECT 1, 'nachher'"+
                "UNION SELECT 2, 'vorher'"+
                "UNION SELECT 3, 'mit';";
        db.execSQL(CREATE_INIT_VALUES);
    }

    private void createDataTable(SQLiteDatabase db) {
        String CREATE_DATA_TABLE = "CREATE TABLE "+this.TABLE_MEDI_DATA + " (" +
                this.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                this.COLUMN_DESC + " INTEGER, " +
                this.COLUMN_DURATION+ " INTEGER, " +
                this.COLUMN_AMOUNT + " INTEGER, "+
                this.COLUMN_WIDTH + " INTEGER, "+
                this.COLUMN_LENGTH + " INTEGER, "+
                this.COLUMN_PICTURE + " TEXT, " +
                this.COLUMN_CREATEDATE+ " TEXT, " +
                this.COLUMN_NOTE+ " TEXT, " +
                this.COLUMN_ACTIVE + " INTEGER " +
                ");";
        db.execSQL(CREATE_DATA_TABLE);
    }

    public void createConsumeIndividualTable (SQLiteDatabase db){
        String CREATE_ConsIndiv_TABLE = "CREATE TABLE " +this.TABLE_MEDI_CONSINDIV + " ("+
                this.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                this.COLUMN_MEDIID + " INTEGER, "+
                this.COLUMN_CONSTIME + " TEXT, "+
                this.COLUMN_EATPART + " INTEGER, "+
                this.COLUMN_DAYPART + " INTEGER, "+
                "FOREIGN KEY("+ this.COLUMN_EATPART +") REFERENCES "+
                this.TABLE_MEDI_EAT+"("+this.COLUMN_ID+")  ON DELETE CASCADE "+
                "FOREIGN KEY("+ this.COLUMN_MEDIID +") REFERENCES "+
                this.TABLE_MEDI_DATA+"("+this.COLUMN_ID+")  ON DELETE CASCADE "+
                "FOREIGN KEY("+ this.COLUMN_DAYPART +") REFERENCES "+
                this.TABLE_MEDI_DAY+"("+this.COLUMN_ID+")  ON DELETE CASCADE "+
                ");";
        db.execSQL(CREATE_ConsIndiv_TABLE);
    }

    public void createConsumeIntervalTable(SQLiteDatabase db){
        String CREATE_ConsInt_TABLE = "CREATE TABLE " + this.TABLE_MEDI_CONSINTER + " ("+
                this.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                this.COLUMN_MEDIID + " INTEGER, "+
                this.COLUMN_STARTTIME + " TEXT, "+
                this.COLUMN_ENDTIME + " TEXT, "+
                this.COLUMN_INTERVAL + " INTEGER, "+
                this.COLUMN_WEEKDAY + " INTEGER, "+
                "FOREIGN KEY("+ this.COLUMN_MEDIID +") REFERENCES "+
                this.TABLE_MEDI_DATA+"("+this.COLUMN_ID+")  ON DELETE CASCADE"+
                ");";
        db.execSQL(CREATE_ConsInt_TABLE);
    }

    private void createStatusTable(SQLiteDatabase db) {
        String CREATE_STATUS_TABLE = "CREATE TABLE "+this.TABLE_MEDI_STATUS + " (" +
                this.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                this.COLUMN_DESC + " INTEGER);";
        db.execSQL(CREATE_STATUS_TABLE);

        String CREATE_INIT_VALUES = "INSERT INTO "+this.TABLE_MEDI_STATUS + " "+
                "Select 0, 'eingenommen'"+
                "UNION SELECT 1, 'verloren'"+
                "UNION SELECT 2, 'vergessen';";
        db.execSQL(CREATE_INIT_VALUES);
    }

    private void createConsumeTable(SQLiteDatabase db) {
        String CREATE_CONSUME_TABLE = "CREATE TABLE "+this.TABLE_MEDI_CONSUMED +"("+
                this.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                this.COLUMN_MEDIID + " INTEGER, "+
                this.COLUMN_POINTINTIME + " TEXT, "+
                this.COLUMN_STATUS + " INTEGER,"+
                "FOREIGN KEY("+ this.COLUMN_MEDIID +") REFERENCES "+
                this.TABLE_MEDI_DATA+"("+this.COLUMN_ID+") ON DELETE CASCADE, " +
                "FOREIGN KEY("+ this.COLUMN_STATUS +") REFERENCES "+
                this.TABLE_MEDI_STATUS+"("+this.COLUMN_ID+")  ON DELETE SET NULL" +
                ");";
        db.execSQL(CREATE_CONSUME_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        if(!db.isReadOnly()){
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
