package YodleeSrc;

public interface IFileTransferConstants {

	public final String AccountCategoryPrompt = "Enter 1 -> Banking | 2 -> Brokerage : ";

	public final String AccountCategoryReEnterPrompt = "Invalid Entry.. Enter 1 -> Banking | 2 -> Brokerage or 0 to Quit : ";
	
	public final String EnterPrompt = "Enter ";
	
	public final String InvalidEnterPrompt = "Invalid Entry.. Enter ";
	
	public final String EXIT_VALUE = "-1";
	
	public final String ReEnterPrompt = "Re-Enter ";
	
	public final String EXIT_STRING = " or " + EXIT_VALUE + " to Quit : ";
	
	public final String AccountNumberPrompt = EnterPrompt + "Account Number : ";

	public final String AccountNumberReEnterPrompt = InvalidEnterPrompt
			+ AccountNumberPrompt + EXIT_STRING;

	public final String ReAccountNumberEnterPrompt = ReEnterPrompt
	+ "Account Number : ";

	public final String ReAccountNumberReEnterPrompt = "Both the Account Numbers do not match..."
	+ ReAccountNumberEnterPrompt + EXIT_STRING;
	
	public final String AccountTypePrompt = "Enter 1 -> Saving | 2 -> Checking : ";

	public final String AccountTypeReEnterPrompt = "Invalid Entry.. Enter 1 -> Saving | 2 -> Checking or 0 to Quit : ";

	public final String RoutingNumberPrompt = EnterPrompt + "Routing Number : ";

	public final String RoutingNumberReEnterPrompt = InvalidEnterPrompt
			+ RoutingNumberPrompt + EXIT_STRING;
	
	public final String BankNamePrompt = EnterPrompt + "Bank Name : ";

	public final String BankNameReEnterPrompt = InvalidEnterPrompt
			+ BankNamePrompt + EXIT_STRING;
	
	public final String SumInfoIDPrompt = EnterPrompt + "Sum Info ID : ";
	
	public final String AmountPrompt = EnterPrompt + "Transfer Amount : ";

	public final String SumInfoIDReEnterPrompt = InvalidEnterPrompt
			+ AmountPrompt + EXIT_STRING;
	
	public final String TransAcctID2Prompt = EnterPrompt
	+ "Transfer Account ID : ";

	public final String TransAcctID2ReEnterPrompt = InvalidEnterPrompt
	+ TransAcctID2Prompt + EXIT_STRING;
	
	public final String ItemIdPrompt = EnterPrompt + "ItemId to be Enabled : ";

	public final String ReItemIdPrompt = InvalidEnterPrompt + ItemIdPrompt
			+ EXIT_STRING;
}
