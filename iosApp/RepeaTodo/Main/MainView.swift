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
    @Environment(\.isPreview) var isPreview
    @Environment(\.navigator) var navigator: MainNavigatorObservableObject
    @Environment(\.previewEnvironment) var previewEnvironment: PreviewEnvironment?

    @ObservedObject private var viewModel: MainViewModel
    @State var presentSideMenu = false

    var body: some View {
        NavigationView {
            ZStack {
                VStack() {
                    HStack{
                        Button{
                            presentSideMenu.toggle()
                        } label: {
                            Image(systemName: "list.bullet")
                                .resizable()
                                .frame(width: 32, height: 32)
                        }
                        Spacer()
                    }
                    Toggle("Show completed", isOn: viewModel.binding(\.isShowCompleted))
                        .padding(.all)
                    Toggle("Show only high priority", isOn: viewModel.binding(\.isShowOnlyHighestPriority))
                        .padding(.all)
                    Spacer()
                    Divider()
                    TasksListView.factory(previewEnvironment)
                        .environmentObject(navigator)
                    Divider()
                    AddTaskInlinedView.factory(previewEnvironment)
                        .padding()
                }
                
                SideMenu(
                    isShowing: $presentSideMenu,
                    content: AnyView(
                        SideMenuView(
                            presentSideMenu: $presentSideMenu
                        )
                    )
                )
            }
        }
        .navigationTitle("Todos")
        .handleNavigation(navigator)
    }
    
    static func factory(_ previewEnvironment: PreviewEnvironment? = nil) -> some View {
        let di = MainComponent()
        let navigator = (previewEnvironment?.get() ?? di.mainNavigator()).asObservableObject()
        let viewModel: MainViewModel = previewEnvironment?.get() ?? di.mainViewModel()
        return MainView(viewModel: viewModel)
            .navigator(navigator)
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView.factory(preview{ $0.mainScreenFakes() })
    }
}
