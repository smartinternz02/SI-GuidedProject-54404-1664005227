package dev.jahidhasanco.groceryapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jahidhasanco.groceryapp.data.local.db.GroceryDatabase
import dev.jahidhasanco.groceryapp.data.local.model.GroceryItems
import dev.jahidhasanco.groceryapp.databinding.ActivityMainBinding
import dev.jahidhasanco.groceryapp.domain.repository.GroceryRepository
import dev.jahidhasanco.groceryapp.domain.viewmodel.GroceryViewModel
import dev.jahidhasanco.groceryapp.domain.viewmodel.GroceryViewModelFactory
import dev.jahidhasanco.groceryapp.presentation.adapter.GroceryAdapter

class MainActivity : AppCompatActivity(), GroceryAdapter.GroceryItemClickInterface {

    private lateinit var binding: ActivityMainBinding

    lateinit var list: List<GroceryItems>
    private lateinit var groceryAdapter: GroceryAdapter
    private lateinit var groceryViewModal: GroceryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        list = ArrayList<GroceryItems>()
        groceryAdapter = GroceryAdapter(list, this)
        binding.recView.layoutManager = LinearLayoutManager(this)
        binding.recView.adapter = groceryAdapter

        val groceryRepository = GroceryRepository(GroceryDatabase(this))
        val factory = GroceryViewModelFactory(groceryRepository)

        groceryViewModal = ViewModelProvider(this, factory)[GroceryViewModel::class.java]
        groceryViewModal.getAllGroceryItems().observe(this, Observer {
            groceryAdapter.list = it
            groceryAdapter.notifyDataSetChanged()
        })

        binding.addBtn.setOnClickListener {
            openDialogBox()
        }

    }

    fun openDialogBox() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.add_dialog)
        val cancelBtn = dialog.findViewById<Button>(R.id.idBtnCancel)
        val addBtn = dialog.findViewById<Button>(R.id.idBtnAdd)
        val itemEdt = dialog.findViewById<EditText>(R.id.idEdtItemName)
        val itemPriceEdt = dialog.findViewById<EditText>(R.id.idEdtItemPrice)
        val itemQuantityEdt = dialog.findViewById<EditText>(R.id.idEdtItemQuantity)
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        addBtn.setOnClickListener {
            val itemName: String = itemEdt.text.toString()
            val itemPrice: String = itemPriceEdt.text.toString()
            val itemQuantity: String = itemQuantityEdt.text.toString()
            val qty: Int = itemQuantity.toInt()
            val pr: Int = itemPrice.toInt()
            if (itemName.isNotEmpty() && itemPrice.isNotEmpty() && itemQuantity.isNotEmpty()) {
                val items = GroceryItems(itemName, qty, pr)
                groceryViewModal.insert(items)
                Toast.makeText(applicationContext, "Item Added", Toast.LENGTH_SHORT).show()
                groceryAdapter.notifyDataSetChanged()
                dialog.dismiss()
            } else {
                Toast.makeText(
                    applicationContext, "Enter All Info", Toast.LENGTH_SHORT
                ).show()

            }

        }
        dialog.show()
    }


    override fun onItemClick(groceryItems: GroceryItems) {
        groceryViewModal.delete(groceryItems)
        groceryAdapter.notifyDataSetChanged()
        Toast.makeText(applicationContext, "Item Deleted", Toast.LENGTH_SHORT).show()
    }
}