package com.ptithcm.frontend.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ECommerceLocal.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_LOCAL_ORDERS = "local_orders";
    public static final String TABLE_LOCAL_ORDER_ITEMS = "local_order_items";

    // Common column names
    public static final String COLUMN_ID = "id";

    // local_orders columns
    public static final String COLUMN_ORDER_TOTAL_AMOUNT = "total_amount";
    public static final String COLUMN_ORDER_STATUS = "status";
    public static final String COLUMN_ORDER_SHIPPING_ADDRESS = "shipping_address";
    public static final String COLUMN_ORDER_PAYMENT_METHOD = "payment_method";
    public static final String COLUMN_ORDER_DATE = "order_date";

    // local_order_items columns
    public static final String COLUMN_ITEM_ORDER_ID = "order_id";
    public static final String COLUMN_ITEM_PRODUCT_ID = "product_id";
    public static final String COLUMN_ITEM_PRODUCT_NAME = "product_name";
    public static final String COLUMN_ITEM_QUANTITY = "quantity";
    public static final String COLUMN_ITEM_UNIT_PRICE = "unit_price";
    public static final String COLUMN_ITEM_SUBTOTAL = "subtotal";

    // Table Create Statements
    private static final String CREATE_TABLE_LOCAL_ORDERS = "CREATE TABLE "
            + TABLE_LOCAL_ORDERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_ORDER_TOTAL_AMOUNT + " REAL,"
            + COLUMN_ORDER_STATUS + " TEXT,"
            + COLUMN_ORDER_SHIPPING_ADDRESS + " TEXT,"
            + COLUMN_ORDER_PAYMENT_METHOD + " TEXT,"
            + COLUMN_ORDER_DATE + " TEXT"
            + ")";

    private static final String CREATE_TABLE_LOCAL_ORDER_ITEMS = "CREATE TABLE "
            + TABLE_LOCAL_ORDER_ITEMS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ITEM_ORDER_ID + " INTEGER,"
            + COLUMN_ITEM_PRODUCT_ID + " INTEGER,"
            + COLUMN_ITEM_PRODUCT_NAME + " TEXT,"
            + COLUMN_ITEM_QUANTITY + " INTEGER,"
            + COLUMN_ITEM_UNIT_PRICE + " REAL,"
            + COLUMN_ITEM_SUBTOTAL + " REAL,"
            + "FOREIGN KEY(" + COLUMN_ITEM_ORDER_ID + ") REFERENCES " + TABLE_LOCAL_ORDERS + "(" + COLUMN_ID + ")"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables
        db.execSQL(CREATE_TABLE_LOCAL_ORDERS);
        db.execSQL(CREATE_TABLE_LOCAL_ORDER_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCAL_ORDERS);

        // Create tables again
        onCreate(db);
    }

    public void clearOrders() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCAL_ORDER_ITEMS, null, null);
        db.delete(TABLE_LOCAL_ORDERS, null, null);
        db.close();
    }

    public void saveOrders(java.util.List<com.ptithcm.frontend.network.dto.OrderResponseDto> orders) {
        if (orders == null || orders.isEmpty()) return;
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            clearOrders(); // Clear existing cache first
            for (com.ptithcm.frontend.network.dto.OrderResponseDto order : orders) {
                android.content.ContentValues orderValues = new android.content.ContentValues();
                orderValues.put(COLUMN_ID, order.id);
                orderValues.put(COLUMN_ORDER_TOTAL_AMOUNT, order.totalAmount != null ? order.totalAmount.doubleValue() : 0.0);
                orderValues.put(COLUMN_ORDER_STATUS, order.status);
                orderValues.put(COLUMN_ORDER_SHIPPING_ADDRESS, order.shippingAddress);
                orderValues.put(COLUMN_ORDER_PAYMENT_METHOD, order.paymentMethod);
                orderValues.put(COLUMN_ORDER_DATE, order.orderDate);
                db.insert(TABLE_LOCAL_ORDERS, null, orderValues);

                if (order.items != null) {
                    for (com.ptithcm.frontend.network.dto.CartItemDto item : order.items) {
                        android.content.ContentValues itemValues = new android.content.ContentValues();
                        itemValues.put(COLUMN_ITEM_ORDER_ID, order.id);
                        itemValues.put(COLUMN_ITEM_PRODUCT_ID, item.productId);
                        itemValues.put(COLUMN_ITEM_PRODUCT_NAME, item.productName);
                        itemValues.put(COLUMN_ITEM_QUANTITY, item.quantity);
                        itemValues.put(COLUMN_ITEM_UNIT_PRICE, item.unitPrice != null ? item.unitPrice.doubleValue() : 0.0);
                        itemValues.put(COLUMN_ITEM_SUBTOTAL, item.subtotal != null ? item.subtotal.doubleValue() : 0.0);
                        db.insert(TABLE_LOCAL_ORDER_ITEMS, null, itemValues);
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public java.util.List<com.ptithcm.frontend.network.dto.OrderResponseDto> getOrders() {
        java.util.List<com.ptithcm.frontend.network.dto.OrderResponseDto> orders = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.query(TABLE_LOCAL_ORDERS, null, null, null, null, null, COLUMN_ORDER_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                com.ptithcm.frontend.network.dto.OrderResponseDto order = new com.ptithcm.frontend.network.dto.OrderResponseDto();
                order.id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                order.totalAmount = java.math.BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL_AMOUNT)));
                order.status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS));
                order.shippingAddress = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_SHIPPING_ADDRESS));
                order.paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_PAYMENT_METHOD));
                order.orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));
                orders.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orders;
    }

    public com.ptithcm.frontend.network.dto.OrderResponseDto getOrderById(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        android.database.Cursor cursor = db.query(TABLE_LOCAL_ORDERS, null, COLUMN_ID + "=?", new String[]{String.valueOf(orderId)}, null, null, null);
        
        com.ptithcm.frontend.network.dto.OrderResponseDto order = null;
        if (cursor.moveToFirst()) {
            order = new com.ptithcm.frontend.network.dto.OrderResponseDto();
            order.id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            order.totalAmount = java.math.BigDecimal.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_ORDER_TOTAL_AMOUNT)));
            order.status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_STATUS));
            order.shippingAddress = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_SHIPPING_ADDRESS));
            order.paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_PAYMENT_METHOD));
            order.orderDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ORDER_DATE));
            
            // Get items
            order.items = new java.util.ArrayList<>();
            android.database.Cursor itemCursor = db.query(TABLE_LOCAL_ORDER_ITEMS, null, COLUMN_ITEM_ORDER_ID + "=?", new String[]{String.valueOf(orderId)}, null, null, null);
            if (itemCursor.moveToFirst()) {
                do {
                    com.ptithcm.frontend.network.dto.CartItemDto item = new com.ptithcm.frontend.network.dto.CartItemDto();
                    item.productId = itemCursor.getLong(itemCursor.getColumnIndexOrThrow(COLUMN_ITEM_PRODUCT_ID));
                    item.productName = itemCursor.getString(itemCursor.getColumnIndexOrThrow(COLUMN_ITEM_PRODUCT_NAME));
                    item.quantity = itemCursor.getInt(itemCursor.getColumnIndexOrThrow(COLUMN_ITEM_QUANTITY));
                    item.unitPrice = java.math.BigDecimal.valueOf(itemCursor.getDouble(itemCursor.getColumnIndexOrThrow(COLUMN_ITEM_UNIT_PRICE)));
                    item.subtotal = java.math.BigDecimal.valueOf(itemCursor.getDouble(itemCursor.getColumnIndexOrThrow(COLUMN_ITEM_SUBTOTAL)));
                    order.items.add(item);
                } while (itemCursor.moveToNext());
            }
            itemCursor.close();
        }
        cursor.close();
        db.close();
        return order;
    }
}
