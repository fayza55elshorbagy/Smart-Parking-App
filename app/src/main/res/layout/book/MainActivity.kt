package com.badr.book


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    lateinit var mRecyclerView: RecyclerView
    lateinit var mRf: DatabaseReference
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = FirebaseDatabase.getInstance().reference
        mRecyclerView = findViewById(R.id.recyclerView)



        mRf= FirebaseDatabase.getInstance().getReference()
            .child("ParkirApp").child("GARAGE").child("FCI_Garage").child("Images")

        val options = FirebaseRecyclerOptions.Builder<Model>()
            .setQuery(mRf,Model::class.java)
            .build()

        val FirebaseRecyclerAdapter =object :FirebaseRecyclerAdapter<Model,MyViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val itemView = LayoutInflater.from(this@MainActivity).inflate(R.layout.card_view, parent, false)
                return MyViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: MyViewHolder, p1: Int, model: Model) {
                val redid = getRef(p1).key.toString()
                mRf.child(redid).addValueEventListener(object  :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        Picasso.get().load(model.Image).into(holder.img)
                    }

                })
            }

        }


        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.adapter= FirebaseRecyclerAdapter
        mRecyclerView.addItemDecoration(CirclePageIndicatorDecoration())
        FirebaseRecyclerAdapter.startListening()


        database.child("ParkirApp").child("GARAGE").child("FCI_Garage")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    val text = p0.message
                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {

                    val Name = p0.child("Name").getValue(String::class.java)
                    grage_name.text = Name
                    val address = p0.child("Address1").getValue(String::class.java)
                   location.text = address

                    val pay = p0.child("HourPrice").getValue(String::class.java)
                    price.text = "pay "+pay+"$ per hour"

                    val fine = p0.child("Fine").getValue(String::class.java)
                    Fine.text = "for late "+ fine+ "$ per hour"

                    val clean =p0.child("cleaning").getValue(String::class.java)
                    washing.text=clean

                    val r = p0.child("rate").getValue(Float::class.java)
                    rate.text = r!!.roundToInt().toString()+"/5 "

                }
            })

        database.child("ParkirApp").child("iot").child("FCI_Garage")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    val text = p0.message
                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {

                    val free = p0.child("free").getValue(Int::class.java)
                    if (free!! > 0){
                    available.text = "YES"
                }else
                    {
                        available.text = "NO"
                    }

                }
            })

       Book.setOnClickListener {
           val i_start_date = Intent(this@MainActivity  , StartDate::class.java)
           startActivity(i_start_date)
       }


    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var img: ImageView = itemView.findViewById(R.id.imageView)
    }


}
