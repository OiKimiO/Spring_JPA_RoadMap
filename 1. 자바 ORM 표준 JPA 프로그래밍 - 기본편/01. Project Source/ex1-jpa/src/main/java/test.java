public class test {
    public static void main(String[] args) {
        int n = 110011;
        int k = 10;
        int total = n;
        int lastNumIdx = 0;
        long isNumber = 0L;
        int answer = 0;
        StringBuffer strNumber = new StringBuffer();
        StringBuffer PrimeNumber = new StringBuffer();

        while(total / 3 > 0){
            strNumber.append(total%3);
            total /= 3;
        }

        strNumber.append(total);
        lastNumIdx = strNumber.length() - 1;

        for(int i = lastNumIdx; i >= 0; i--){
            // 첫번째 경우
            if(i <= lastNumIdx-1 && i > 0){
               if(strNumber.charAt(i-1) == '0' && strNumber.charAt(i+1) == '0' && strNumber.charAt(i) != '1'){
                   isNumber = Long.parseLong(String.valueOf(strNumber.charAt(i)));
                   PrimeNumber.delete(0,PrimeNumber.length());
                   if(isPrime(isNumber)){
                       answer++;
                   }
                   continue;
               }
            }

            // 2번째 경우
            if(i > 0){
                if(strNumber.charAt(i-1) == '0' && strNumber.charAt(i) != '0'){
                    PrimeNumber.append(strNumber.charAt(i));
                    isNumber = Long.parseLong(String.valueOf(strNumber.charAt(i)));
                    if(isNumber > 1) {
                        PrimeNumber.delete(0, PrimeNumber.length());
                        if (isPrime(isNumber)) {
                            answer++;
                        }
                    }
                    continue;
                }
            }

            // 3번째 경우
            if(i <= lastNumIdx-1){
                if(strNumber.charAt(i+1) == '0' && strNumber.charAt(i) != '0'){
                    PrimeNumber.append(strNumber.charAt(i));
                    isNumber = Long.parseLong(String.valueOf(strNumber.charAt(i)));
                    if(isNumber > 1L) {
                        PrimeNumber.delete(0, PrimeNumber.length());
                        if (isPrime(isNumber)) {
                            answer++;
                        }
                    }
                    continue;
                }
            }

            // 4번째 경우
            if(PrimeNumber.toString().indexOf('0') > 0){
                PrimeNumber.delete(0,PrimeNumber.length());
                continue;
            }

            if(strNumber.charAt(i) == '0'){
                continue;
            }

            PrimeNumber.append(strNumber.charAt(i));

        }

        System.out.println(answer);

    }

    private static boolean isPrime(long isNumber) {
        if(isNumber == 1){
            return false;
        }

        long total = (long) Math.sqrt(isNumber);

        for(int i = 2; i < total; i++){
            if(isNumber % i == 0){
                return false;
            }
        }

        return true;
    }
}
