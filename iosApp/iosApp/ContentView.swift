import SwiftUI
import shared
import UIKit

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        autoreleasepool{
            Main_iosKt.MainViewController()
        }
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}


struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ComposeView()
	}
}
