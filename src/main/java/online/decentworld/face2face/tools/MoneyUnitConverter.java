package online.decentworld.face2face.tools;

/**
 * 主要进行单位转换
 * 分-元
 * @author Sammax
 *
 */
public class MoneyUnitConverter {

	
	public static int fromYuanToFen(float yuan){
		return (int)yuan*100;
	}
	
	public static String fromYuanToFenStr(float yuan){
		return String.valueOf((int)yuan*100);
	}
	
	public static String fromFenToYuan(String amount){
		StringBuilder sb=new StringBuilder();
		int i=sb.append(amount).indexOf(".");
		if(amount.equals("0")){
			return "0.00";
		}
		if(i!=-1){
			throw new RuntimeException();
		}else{
			return sb.insert(sb.length()-2, ".").toString();
			
		}
	}
	
	
//	public static String fromFenToYuan(Wealth wealth){
//		String str=String.valueOf(wealth.getWealth());
//		StringBuilder sb=new StringBuilder();
//		return sb.append(str).insert(str.length()-2, ".").toString();
//	}

	public static String fromFenToYuanStr(int wealth){
		if(wealth==0){
			return "0.00";
		}else{
			String str=String.valueOf(wealth);
			StringBuilder sb=new StringBuilder();
			if(str.length()>2){
				return sb.append(str).insert(str.length()-2, ".").toString();
			}else{
				sb.append("0.");
				if(str.length()==1){
					sb.append("0").append(str);
				}else{
					sb.append(str);
				}
				return sb.toString();
			}
			
		}
	}
	public static void main(String[] args) {
		System.out.println(MoneyUnitConverter.fromFenToYuanStr(1));
	}
	
	public static int fromYuantoFen(String money){
		StringBuilder sb=new StringBuilder();
		int i=sb.append(money).indexOf(".");
		if(i==-1){
			return Integer.parseInt(sb.append("00").toString());
		}else{
			if(i==sb.length()-3){
				return Integer.parseInt(sb.deleteCharAt(i).toString());
			}else if(i==sb.length()-2){
				return Integer.parseInt(sb.deleteCharAt(i).append("0").toString());
			}else{
				throw new RuntimeException();
			}
		}
	}

	public static String fromYuantoFenStr(String money){
		StringBuilder sb=new StringBuilder();
		int i=sb.append(money).indexOf(".");
		if(i==-1){
			return sb.append("00").toString();
		}else{
			if(i==sb.length()-3){
				return sb.deleteCharAt(i).toString();
			}else if(i==sb.length()-2){
				return sb.deleteCharAt(i).append("0").toString();
			}else{
				throw new RuntimeException();
			}
		}
	}
	
	
}
