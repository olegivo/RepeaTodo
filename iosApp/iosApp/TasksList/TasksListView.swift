//
//  TasksListView.swift
//  iosApp
//
//  Created by Олег Иващенко on 19.06.2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import Combine
import shared

struct TasksListView: View {
    @Environment(\.isPreview) var isPreview
    
    @ObservedObject private var viewModel: TasksListViewModel
    
    @EnvironmentObject
    private var navigator: MainNavigatorObservableObject
    
    var body: some View {
        ScrollView {
            LazyVStack(alignment: .leading) {
                ForEach(viewModel.tasks) { task in
                    TasksListItemView(
                        task: task,
                        onTaskEditClicked: { viewModel.onTaskEditClicked(task: $0) },
                        onCompleteTaskClicked: { viewModel.onTaskCompletionClicked(task: $0) }
                    )
                    Divider()
                }
            }
            .cornerRadius(CGFloat(12))
            .padding()
        }
    }
    
    static func factory(_ previewEnvironment: PreviewEnvironment? = nil) -> TasksListView {
        let viewModel: TasksListViewModel = previewEnvironment?.get() ?? TasksListComponent().tasksListViewModel()
        return TasksListView(viewModel: viewModel)
    }
}

private func TasksListItemView(
    task: TaskUi,
    onTaskEditClicked: @escaping (TaskUi) -> Void,
    onCompleteTaskClicked: @escaping (TaskUi) -> Void
) -> some View {
    HStack(alignment: .top) {
        Button (
            action: { onCompleteTaskClicked(task) },
            label: {
                Image(systemName: task.isCompleted ? "checkmark.square" : "square")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .foregroundColor(.gray)
                    .frame(width: 32, height: 32)
                    .padding()
                    .cornerRadius(12)
            }
        )
        VStack(alignment: .leading, spacing: CGFloat(0)) {
            Text(task.title)
                .frame(maxWidth: .infinity, alignment: .leading)
            if let lastCompletionDate = task.lastCompletionDate {
                Text(lastCompletionDate)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }

            Spacer()
        }
        .frame(alignment: .topLeading)
        .padding()
        
        Spacer()
        
        Button (
            action: { onTaskEditClicked(task) },
            label: {
                Image(systemName: "pencil")
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .foregroundColor(.gray)
                    .frame(width: 32, height: 32)
                    .padding()
                    .cornerRadius(12)
                
            }
        )
    }
    .frame(height: CGFloat(80))
}

extension TaskUi: Identifiable {
    public var id: String { uuid }
}

extension TasksListViewModel {
    var tasks: [TaskUi] {
        get {
            return self.state(
                \.state,
                 equals: { $0 === $1 },
                 mapper: { $0.tasks }
            )
        }
    }
}

struct TasksListView_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            TasksListView.factory(preview{ $0.taskListFakes() })
                .environmentObject(FakeMainNavigator().asObservableObject())
        }
        .cornerRadius(CGFloat(12))
    }
}

struct TasksListItemView_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            TasksListItemView(
                task: TaskUi(
                    uuid: "The UUID",
                    title: "Task 1",
                    isCompleted: false,
                    lastCompletionDate: nil
                ),
                onTaskEditClicked: {_ in },
                onCompleteTaskClicked: {_ in }
            )
        }
        .cornerRadius(CGFloat(12))
    }
}

struct TasksListItemView_CompletedPreviews: PreviewProvider {
    static var previews: some View {
        VStack {
            TasksListItemView(
                task: TaskUi(
                    uuid: "The UUID",
                    title: "Task 1",
                    isCompleted: true,
                    lastCompletionDate: "several seconds ago"
                ),
                onTaskEditClicked: {_ in },
                onCompleteTaskClicked: {_ in }
            )
        }
        .cornerRadius(CGFloat(12))
    }
}
