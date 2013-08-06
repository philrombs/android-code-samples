

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.util.Log;

public class FileComparator{
	
	
	//Sort Name in Ascending Order
	public static ArrayList<FileInfo> sortNameAscending(ArrayList<FileInfo> arrayFileInfos) {
		
		//Log.i("FileComparator:sortNameAscending", "Inside sortAscending Function");
		
		//Sort by Name first... regardless of file/folder
		Collections.sort(arrayFileInfos, new Comparator<FileInfo>() {
			public int compare(FileInfo lhs, FileInfo rhs) {
				return lhs.getName().compareToIgnoreCase(rhs.getName());
			}
		});
		
		//Separate folders to the top and files to the bottom next
		Collections.sort(arrayFileInfos, new Comparator<FileInfo>() {
			public int compare(FileInfo lhs, FileInfo rhs) {
				if(lhs.getIsDirectory() == rhs.getIsDirectory()){
					return 0;
				} else if(lhs.getIsDirectory() && !rhs.getIsDirectory()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		return arrayFileInfos;
	}
	
	
	//Sort Name in Descending Order
	public static ArrayList<FileInfo> sortNameDescending(ArrayList<FileInfo> arrayFileInfos) {
		
		//Sort by Name first... regardless of file/folder
		Collections.sort(arrayFileInfos, new Comparator<FileInfo>() {
			public int compare(FileInfo lhs, FileInfo rhs) {
				return rhs.getName().compareToIgnoreCase(lhs.getName());
			}
		});
		
		//Separate folders to the bottom and files to the top next
		Collections.sort(arrayFileInfos, new Comparator<FileInfo>() {
			public int compare(FileInfo lhs, FileInfo rhs) {
				if(rhs.getIsDirectory() == lhs.getIsDirectory()){
					return 0;
				} else if(rhs.getIsDirectory() && !lhs.getIsDirectory()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		return arrayFileInfos;
	}
	
	
	//Sort Size in Ascending Order (0 -> Infinite)
	public static ArrayList<FileInfo> sortSizeAscending(ArrayList<FileInfo> arrayFileInfos) {
		
		//Put Folders at the top and in ABC order
		sortNameAscending(arrayFileInfos);
		
		//Sort files by size from 0-> infinite
		Collections.sort(arrayFileInfos, new Comparator<FileInfo>() {
			public int compare(FileInfo lhs, FileInfo rhs) {
				if(!lhs.getIsDirectory() && !rhs.getIsDirectory()){
					if(lhs.getFileSizeRaw() > rhs.getFileSizeRaw()) {
						return 1;
					} else if (rhs.getFileSizeRaw() == lhs.getFileSizeRaw()) {
						return 0;
					} else {
						return -1;
					}
				}
				
				return 0;
			}
		});
		
		return arrayFileInfos;
	}
	
	
	//Sort Size in Descending Order (Infinite -> 0)
	public static ArrayList<FileInfo> sortSizeDescending(ArrayList<FileInfo> arrayFileInfos) {
		
		//Put Folders at the top and in ABC order
		sortNameAscending(arrayFileInfos);
		
		//Sort files by size from infinite -> 0
		Collections.sort(arrayFileInfos, new Comparator<FileInfo>() {
			public int compare(FileInfo lhs, FileInfo rhs) {
				if(!lhs.getIsDirectory() && !rhs.getIsDirectory()){
					if(rhs.getFileSizeRaw() > lhs.getFileSizeRaw()) {
						return 1;
					} else if (rhs.getFileSizeRaw() == lhs.getFileSizeRaw()) {
						return 0;
					} else {
						return -1;
					}
				}
				
				return 0;
			}
		});
		
		return arrayFileInfos;
	}
	
	
	//Sort by Date First Modified... Folders are on top
	public static ArrayList<FileInfo> sortDateFirstModified(ArrayList<FileInfo> arrayFileInfos) {
		
		//Sort folders and files by first modified -> last modified
		Collections.sort(arrayFileInfos, new Comparator<FileInfo>() {
			public int compare(FileInfo lhs, FileInfo rhs) {
				if(lhs.getlastModifiedDate() > rhs.getlastModifiedDate()) {
					return 1;
				} else if (rhs.getlastModifiedDate() == lhs.getlastModifiedDate()) {
					return 0;
				} else {
					return -1;
				}
				
			}
		});
		
		//Separate folders to the top and files to the bottom next
		Collections.sort(arrayFileInfos, new Comparator<FileInfo>() {
			public int compare(FileInfo lhs, FileInfo rhs) {
				if(lhs.getIsDirectory() == rhs.getIsDirectory()){
					return 0;
				} else if(lhs.getIsDirectory() && !rhs.getIsDirectory()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		return arrayFileInfos;
	}
	
	
	//Sort by Date Last Modified... Folders are on bottom
	public static ArrayList<FileInfo> sortDateLastModified(ArrayList<FileInfo> arrayFileInfos) {
		
		//Sort folders and files by last modified -> first modified
		Collections.sort(arrayFileInfos, new Comparator<FileInfo>() {
			public int compare(FileInfo lhs, FileInfo rhs) {
				if(rhs.getlastModifiedDate() > lhs.getlastModifiedDate()) {
					return 1;
				} else if (rhs.getlastModifiedDate() == lhs.getlastModifiedDate()) {
					return 0;
				} else {
					return -1;
				}
				
			}
		});
		
		//Separate folders to the bottom and files to the top next
		Collections.sort(arrayFileInfos, new Comparator<FileInfo>() {
			public int compare(FileInfo lhs, FileInfo rhs) {
				if(rhs.getIsDirectory() == lhs.getIsDirectory()){
					return 0;
				} else if(rhs.getIsDirectory() && !lhs.getIsDirectory()) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		
		return arrayFileInfos;
	}
	

}
