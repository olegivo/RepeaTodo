import SwiftUI
import shared

struct ContentView: View {
    let mainComponent = MainComponent()
    
    var body: some View {
        MainView.factory()
//        NavigationView {
//            ZStack {
//                Image(systemName: "airplane").resizable()
//                    .aspectRatio(contentMode: .fit)
//                    .opacity(0.1).rotationEffect(.degrees(-90))
//                    .frame(width: 250, height: 250, alignment: .center)
//                VStack(alignment: .leading, spacing: 5) {
//                    Spacer()
//                    NavigationLink(destination: AddTaskView()) {
//                        Text("Добавить задачу")
//                    }
//                    Text(greet)
//                }.font(.title).padding(20)
//                Spacer()
//            }.navigationBarTitle(Text("RepeaTodo"))
//        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
