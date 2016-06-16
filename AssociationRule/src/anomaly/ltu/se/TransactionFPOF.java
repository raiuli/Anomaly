package anomaly.ltu.se;

import java.util.Comparator;
import java.util.List;

public class TransactionFPOF {
	List<Integer> transaction;
	int fpof=0;
	static int tid=0;
	int id;
	public TransactionFPOF(List<Integer> transaction, int fpof) {
		super();
		this.transaction = transaction;
		this.fpof = fpof;
		id=tid++;
	}
	public List<Integer> getTransaction() {
		return transaction;
	}
	public void setTransaction(List<Integer> transaction) {
		this.transaction = transaction;
	}
	public int getFpof() {
		return fpof;
	}
	public void setFpof(int fpof) {
		this.fpof = fpof;
	}
	@Override
	public String toString() {
		String transactionString="";
		for (Integer item : transaction){
			transactionString=transactionString+" "+item;
		}
		return "TransactionFPOF [id="+id+" transaction=(" + transaction + "), fpof=" + fpof + "]";
	}
	
	/*@Override
	public int compare(TransactionFPOF o1, TransactionFPOF o2) {
		if(o1.getFpof()< o2.getFpof()){
            return 1;
        } else {
            return -1;
        }
	}*/
	
}
