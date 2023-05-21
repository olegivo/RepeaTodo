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
import UIKit

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
        VStack(alignment: .leading, spacing: CGFloat(0)) {
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
            if let priority = task.priority {
                Text(priority.title)
                    .padding(.leading, 28)
                    .foregroundColor(getColor(priority))
            }
        }
        VStack(alignment: .leading, spacing: CGFloat(0)) {
            Text(task.title)
                .fixedSize(horizontal: false, vertical: true)
                .lineLimit(2)
                .frame(maxWidth: .infinity, alignment: .leading)

            Spacer()

            if let lastCompletionDate = task.lastCompletionDate {
                Text(lastCompletionDate)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .frame(alignment: .topLeading)
        .padding()
    }
    .frame(height: CGFloat(92))
    .contentShape(Rectangle())
    .gesture(
        TapGesture()
            .onEnded { _ in
                onTaskEditClicked(task)
            }
    )
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
                    lastCompletionDate: nil,
                    priority: nil
                ),
                onTaskEditClicked: {_ in },
                onCompleteTaskClicked: {_ in }
            )
        }
        .cornerRadius(CGFloat(12))
    }
}

struct TasksListItemViewCompleted_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            TasksListItemView(
                task: TaskUi(
                    uuid: "The UUID",
                    title: "Task 1",
                    isCompleted: true,
                    lastCompletionDate: "several seconds ago",
                    priority: nil
                ),
                onTaskEditClicked: {_ in },
                onCompleteTaskClicked: {_ in }
            )
        }
        .cornerRadius(CGFloat(12))
    }
}

struct TasksListItemViewPriorityHigh_Previews: PreviewProvider {
    static var previews: some View {
        VStack {
            TasksListItemView(
                task: TaskUi(
                    uuid: "The UUID",
                    title: "Task 1",
                    isCompleted: false,
                    lastCompletionDate: "several seconds ago",
                    priority: .high
                ),
                onTaskEditClicked: {_ in },
                onCompleteTaskClicked: {_ in }
            )
        }
        .cornerRadius(CGFloat(12))
    }
}

func getColor(_ priority: PriorityInList) -> Color {
    switch priority.priority {
    case .high:
        return .red
    case .medium:
        return .orange
    case .low:
        return .green
    default:
        return .gray
    }
}

extension Color {
    init?(hex2: String) {
        self.init(uiColor: colorWithHexString(hexString: hex2))
    }
    
    init?(hex: String) {
        var hexSanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")

        var rgb: UInt64 = 0

        var r: CGFloat = 0.0
        var g: CGFloat = 0.0
        var b: CGFloat = 0.0
        var a: CGFloat = 1.0

        let length = hexSanitized.count

        guard Scanner(string: hexSanitized).scanHexInt64(&rgb) else { return nil }

        if length == 6 {
            r = CGFloat((rgb & 0xFF0000) >> 16) / 255.0
            g = CGFloat((rgb & 0x00FF00) >> 8) / 255.0
            b = CGFloat(rgb & 0x0000FF) / 255.0

        } else if length == 8 {
            r = CGFloat((rgb & 0xFF000000) >> 24) / 255.0
            g = CGFloat((rgb & 0x00FF0000) >> 16) / 255.0
            b = CGFloat((rgb & 0x0000FF00) >> 8) / 255.0
            a = CGFloat(rgb & 0x000000FF) / 255.0

        } else {
            return nil
        }

        self.init(red: r, green: g, blue: b, opacity: a)
    }
    
      /// Constructing color from hex string
      ///
      /// - Parameter hex: A hex string, can either contain # or not
      public init(
        _ colorSpace: Color.RGBColorSpace = .sRGB,
        hexString: String
      ) {
        var hex =
        hexString.hasPrefix("#")
        ? String(hexString.dropFirst())
        : hexString
        guard hex.count == 3 || hex.count == 6
        else {
          self = .init(colorSpace, white: 1.0, opacity: 0)
          return
        }
        if hex.count == 3 {
          for (index, char) in hex.enumerated() {
            hex.insert(char, at: hex.index(hex.startIndex, offsetBy: index * 2))
          }
        }
        guard let intCode = Int(hex, radix: 16) else {
          self = .init(colorSpace, white: 1.0, opacity: 0)
          return
        }
        
        self = .init(
          colorSpace,
          red: Double((intCode >> 16) & 0xFF) / 255.0,
          green: Double((intCode >> 8) & 0xFF) / 255.0,
          blue: Double((intCode) & 0xFF) / 255.0,
          opacity: 1.0
        )
      }
}

func colorWithHexString(hexString: String) -> UIColor {
    var colorString = hexString.trimmingCharacters(in: .whitespacesAndNewlines)
    colorString = colorString.replacingOccurrences(of: "#", with: "").uppercased()

    print(colorString)
    let alpha: CGFloat = 1.0
    let red: CGFloat = colorComponentFrom(colorString: colorString, start: 0, length: 2)
    let green: CGFloat = colorComponentFrom(colorString: colorString, start: 2, length: 2)
    let blue: CGFloat = colorComponentFrom(colorString: colorString, start: 4, length: 2)

    let color = UIColor(red: red, green: green, blue: blue, alpha: alpha)
    return color
}

func colorComponentFrom(colorString: String, start: Int, length: Int) -> CGFloat {

    let startIndex = colorString.index(colorString.startIndex, offsetBy: start)
    let endIndex = colorString.index(startIndex, offsetBy: length)
    let subString = colorString[startIndex..<endIndex]
    let fullHexString = length == 2 ? subString : "\(subString)\(subString)"
    var hexComponent: UInt32 = 0

    guard Scanner(string: String(fullHexString)).scanHexInt32(&hexComponent) else {
        return 0
    }
    let hexFloat: CGFloat = CGFloat(hexComponent)
    let floatValue: CGFloat = CGFloat(hexFloat / 255.0)
    print(floatValue)
    return floatValue
}
