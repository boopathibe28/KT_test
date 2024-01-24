package com.app.test.realm_db

import android.content.Context
import com.app.test.utils.CartRealmModel
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults

class RealmLibrary {
    fun insertItem(
        context: Context?, empId: String,
        emp_name: String?,
        emp_roll: String?,
        emp_details: String?,
        addOrDelete: String,
        itemId: String,
        qty_: String?
    ) {
        val realm = Realm.getDefaultInstance()
        val updateIngredients = false
        val sameIngredients = false
        var isDelete = false
        realm.beginTransaction()
        val cartRealmModels =
            RealmList<CartRealmModel>() //realm.where(CartRealmModel.class).equalTo("item_id", itemId).findAll();
        var cartRealmModel: CartRealmModel? = null
        if (cartRealmModels.size == 0) {
            cartRealmModel = CartRealmModel()
            val maxId = realm.where(CartRealmModel::class.java).max("id")
            val nextId = if (maxId == null) 1 else maxId.toInt() + 1
            cartRealmModel.id = empId.toInt()
            cartRealmModel.emp_id = empId
            cartRealmModel.emp_name = emp_name
            cartRealmModel.emp_roll = emp_roll
            cartRealmModel.emp_roll = qty_
            cartRealmModel.emp_details = emp_details
        } else {
            for (itemCount in cartRealmModels.indices) {
                cartRealmModel = cartRealmModels[itemCount]
                if (cartRealmModel!!.item_id == itemId) {
                    if (cartRealmModel.qty == "1" && addOrDelete == "2") {
                        cartRealmModel.deleteFromRealm()
                        isDelete = true
                        break
                    } else {
                        val qty =
                            if (addOrDelete == "1") (cartRealmModel.qty.toInt() + 1).toString() else (cartRealmModel.qty.toInt() - 1).toString()
                        cartRealmModel.qty = qty
                    }
                } else {
                    cartRealmModel = CartRealmModel()
                    cartRealmModel.item_id = itemId
                    val maxId = realm.where(CartRealmModel::class.java).max("id")
                    val nextId = if (maxId == null) 1 else maxId.toInt() + 1
                    cartRealmModel.id = nextId
                }
            }
        }
        if (!isDelete) {
            if (sameIngredients) {
                cartRealmModel = CartRealmModel()
                cartRealmModel.item_id = itemId
                cartRealmModel.qty = qty_
                val maxId = realm.where(CartRealmModel::class.java).max("id")
                val nextId = if (maxId == null) 1 else maxId.toInt() + 1
                cartRealmModel.id = nextId
            }
            if (cartRealmModel != null) {
                cartRealmModel.qty = qty_
                cartRealmModel.id = empId.toInt()
                cartRealmModel.emp_id = empId
                cartRealmModel.item_id = itemId
                cartRealmModel.emp_name = emp_name
                cartRealmModel.emp_details = emp_details
                cartRealmModel.emp_roll = emp_roll
                realm.copyToRealmOrUpdate(cartRealmModel)
            } else {
                // MyApplication.displayUnKnownError();
            }
        }
        realm.commitTransaction()
    }

    fun deleteItem(id: Int?) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        val item = realm.where(CartRealmModel::class.java).equalTo("id", id).findFirst()
        item!!.deleteFromRealm()
        realm.commitTransaction()
    }

    fun clearCart() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }

    fun getCartListSize(vendor_key: String?): Int {
        val realm = Realm.getDefaultInstance()
        val cartRealmModels = realm.where(
            CartRealmModel::class.java
        ).equalTo("vendor_key", vendor_key).findAll()
        return cartRealmModels.size
    }

    fun getCartList(vendor_key: String?): RealmResults<CartRealmModel> {
        val realm = Realm.getDefaultInstance()
        return realm.where(CartRealmModel::class.java).equalTo("vendor_key", vendor_key).findAll()
    }

    val cartList: RealmResults<CartRealmModel>
        get() {
            val realm = Realm.getDefaultInstance()
            return realm.where(CartRealmModel::class.java).findAll()
        }
    val allCartListSize: Int
        get() {
            val realm = Realm.getDefaultInstance()
            return realm.where(CartRealmModel::class.java).findAll().size
        }

    fun getItems(item_key: String?): Int {
        val realm = Realm.getDefaultInstance()
        val items = realm.where(CartRealmModel::class.java).equalTo("item_key", item_key).findAll()
        var qty = 0
        for (count in items.indices) {
            qty = qty + items[count]!!.qty.toInt()
        }
        return qty
    }

    fun getAllItems(item_key: String?): RealmResults<CartRealmModel> {
        val realm = Realm.getDefaultInstance()
        return realm.where(CartRealmModel::class.java).equalTo("item_key", item_key).findAll()
    }

    fun updateQty(cartRealmModel: CartRealmModel, currentQty: Int) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        cartRealmModel.qty = currentQty.toString()
        realm.commitTransaction()
    }

    fun updateItemNotes(cartRealmModel: CartRealmModel, name: String?) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        cartRealmModel.emp_name = name
        realm.commitTransaction()
    }

    fun updateCart(
        context: Context?, editCart: CartRealmModel, empId: String?,
        emp_name: String?,
        emp_roll: String?,
        emp_details: String?,
        itemId: String?,
        qty_: String?
    ) {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        editCart.emp_id = empId
        editCart.emp_name = emp_name
        editCart.emp_roll = emp_roll
        editCart.emp_details = emp_details
        editCart.item_id = itemId
        editCart.qty = qty_
        realm.commitTransaction()
    }

    companion object {
        val instance = RealmLibrary()
    }
}