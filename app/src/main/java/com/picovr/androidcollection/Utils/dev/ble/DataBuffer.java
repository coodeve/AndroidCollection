package com.picovr.androidcollection.Utils.dev.ble;


public class DataBuffer {
    /**
     * 对象数组，队列最多存储a.length-1个对象
     */
	byte[] a;

    /**
     * 队首下标
     */
    int front;

    /**
     * 队尾下标
     */
    int rear;

    /**
     * 队列的个数
     */
    int cnt;
    
    public DataBuffer(int size){  
        a = new byte[size];  
        front = 0;  
        rear =0;  
        cnt = 0;
    }

    /**
     * 将一个对象追加到队列尾部
     * @param data  data数组
     * @param len  len数组长度
     * @return  队列满时返回false,否则返回true
     */
    public int enqueue(byte[] data,int len){
    	
    	int i;
    	
    	for ( i=0;i<len;i++) {
    		if((rear+1)%a.length==front)
    		{  
    			return i;  
    		}  
    		a[rear]=data[i];  
    		rear = (rear+1)%a.length;
    		cnt++;
    	}
        return i;  
    }  
    
    /** 
     * 从队列头部开始弹出len个字节 
     * @return 弹出的字节内容
     */  
    public int dequeue(byte[] data_out,int len){  
        if(rear==front){  
            return 0;  
        }  
        
        int i;
        for (i=0;i<len;i++)
        {
        	data_out[i] = a[front];  
        	front = (front+1)%a.length;
        	cnt--;
        	if (rear==front)
        	{
        		return i;
        	}
        }
       	return i;  
    }
    
    /**
     *获取字节长度
     */
    public int getSize(){
		return cnt;
    }

    
}

