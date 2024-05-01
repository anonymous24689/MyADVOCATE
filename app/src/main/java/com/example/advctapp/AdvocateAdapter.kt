//import com.example.advctapp.AdvocateInfo
//
//class AdvocateAdapter(context: Context, private val advocateList: ArrayList<AdvocateInfo>, private val consultationManager: ConsultationManager) :
//    RecyclerView.Adapter<AdvocateAdapter.AdvocateViewHolder>() {
//
//    private val mContext: Context = context
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvocateViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.case_card, parent, false)
//        return AdvocateViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: AdvocateViewHolder, position: Int) {
//        val advocate = advocateList[position]
//
//        holder.advocateName.text = advocate.username
//        holder.specialization.text = advocate.specialization
//        holder.contact.text = advocate.mobileNo
//
//        // Consult Button Click Listener
//        holder.consultButton.setOnClickListener {
//            // Send consultation request to the advocate
//            consultationManager.sendConsultationRequest(advocate.username)
//            Toast.makeText(mContext, "Sending consultation request...", Toast.LENGTH_SHORT).show()
//        }
//
//        // Call Button Click Listener
//        holder.callButton.setOnClickListener {
//            val mobileNo = advocate.mobileNo
//            if (mobileNo.isNotEmpty()) {
//                val intent = Intent(Intent.ACTION_DIAL)
//                intent.data = Uri.parse("tel:$mobileNo")
//                mContext.startActivity(intent)
//            } else {
//                Toast.makeText(mContext, "No phone number available", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Location Button Click Listener
//        holder.locationButton.setOnClickListener {
//            val address = advocate.address // Assuming address is provided
//            if (address.isNotEmpty()) {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$address"))
//                // Check if map app is available before starting activity
//                if (intent.resolveActivity(mContext.packageManager) != null) {
//                    mContext.startActivity(intent)
//                } else {
//                    Toast.makeText(mContext, "No map app found", Toast.LENGTH_SHORT).show()
//                }
//            } else {
//                Toast.makeText(mContext, "No address available", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return advocateList.size
//    }
//
//    fun setData(newList: ArrayList<AdvocateInfo>, callback: () -> Unit) {
//        advocateList.clear()
//        advocateList.addAll(newList)
//        notifyDataSetChanged()
//        callback()
//    }
//
//    class AdvocateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val advocateImage: ImageView = itemView.findViewById(R.id.advocateImage)
//        val advocateName: TextView = itemView.findViewById(R.id.advocateName)
//        val specialization: TextView = itemView.findViewById(R.id.specialization)
//        val contact: TextView = itemView.findViewById(R.id.contact)
//        val consultButton: Button = itemView.findViewById(R.id.consultButton)
//        val callButton: Button = itemView.findViewById(R.id.callButton)
//        val locationButton: Button = itemView.findViewById(R.id.locationButton)
//    }
//}
