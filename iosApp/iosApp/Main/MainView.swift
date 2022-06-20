//
//  MainView.swift
//  iosApp
//
//  Created by Олег Иващенко on 16.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import shared

struct MainView: View {
    @StateObject
    private var viewModel: MainViewModelObservableObject
    @StateObject
    private var navigator: MainNavigatorObservableObject

    var body: some View {
        NavigationView {
            ZStack(alignment: .bottomTrailing) {
                TasksListView.factory()
                Button(action: {
                    viewModel.onAddTaskClicked()
                }) {
                    Image(systemName: "plus")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .foregroundColor(.white)
                        .frame(width: 24, height: 24)
                        .padding()
                        .background(Color.accentColor)
                        .cornerRadius(12)
                }
                .padding([.bottom, .trailing])
            }
        }
        .navigationTitle("Todos")
        .handleNavigation($navigator.navigationDirection)
    }
    
    static func factory() -> MainView {
        let di = MainComponent()
        return MainView(viewModel: di.mainViewModel().asObservableObject(), navigator: di.mainNavigator().asObservableObject())
    }
}

//struct MainView_Previews: PreviewProvider {
//    static var previews: some View {
//        MainView()
//    }
//}
