package com.example.project;

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    lateinit var mRecyclerView: RecyclerView
    lateinit var mRf: DatabaseReference
    private lateinit var database: DatabaseReference
    var mId: ArrayList<Book>? = null
    val database1 = FirebaseDatabase.getInstance()
    val myRef = database1.reference
    var mAth : FirebaseAuth?= null


    override fun onStart() {
        super.onStart()
        mAth= FirebaseAuth.getInstance()
        myRef.child("ParkirApp").child("users").child("FCI_Garage")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    val text = p0.message
                    Toast.makeText(this@MainActivity, text, Toast.LENGTH_LONG).show()
                }

                override fun onDataChange(p0: DataSnapshot) {
                    for (Data in p0.children) {
                        val tf = Data.getValue(com.example.project.Book::class.java)
                        mId?.add(tf!!)
                    }
                }

            }

            )}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       mId = ArrayList()
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

                    val extra = p0.child("ExtraHours").getValue(String::class.java)


                    val pay = p0.child("HourPrice").getValue(String::class.java)
                    price.text = pay+" $ Per Hour \n "+extra+" $ Extra Hour"


                    val fine = p0.child("Fine").getValue(String::class.java)
                    Fine.text = "For late "+ fine+ "$"


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
                        available.text = "Not Now\n  but reservation is allowed"

                    }

                }
            })

       Book.setOnClickListener {
           if (mId != null) {
               for (i in mId!!) {

                   if (i.ID == mAth!!.currentUser!!.uid) {
                       checkuser()
                   } else {
                       Handler().postDelayed({
                           //start welcome activity
                           startActivity(Intent(this@MainActivity, StartDate::class.java))
                           //finish this activity
                           finish()
                       }, 500)
                   }

               }
           }
           else
           {
               Handler().postDelayed({
                   //start welcome activity
                   startActivity(Intent(this@MainActivity, StartDate::class.java))
                   //finish this activity
                   finish()
               }, 1000)
           }
       }


       }




    fun checkuser(){
        val mAlertDialog = AlertDialog.Builder(this@MainActivity)
        mAlertDialog.setTitle("WARNING! ") //set alertdialog title
        mAlertDialog.setMessage("You already have Reservation appointement!") //set alertdialog message
        mAlertDialog.setPositiveButton("Ok") { dialog, id ->

        }
        mAlertDialog.show()
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var img: ImageView = itemView.findViewById(R.id.imageView)
    }



}
